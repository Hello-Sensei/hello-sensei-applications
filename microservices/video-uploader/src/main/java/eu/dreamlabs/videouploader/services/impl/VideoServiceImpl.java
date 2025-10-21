package eu.dreamlabs.videouploader.services.impl;

import com.github.kokorin.jaffree.StreamType;
import com.github.kokorin.jaffree.ffprobe.FFprobe;
import com.github.kokorin.jaffree.ffprobe.FFprobeResult;
import com.github.kokorin.jaffree.ffprobe.Stream;
import eu.dreamlabs.videouploader.config.RabbitMQUtil;
import eu.dreamlabs.videouploader.domain.enums.VideoResolution;
import eu.dreamlabs.videouploader.domain.enums.VideoStatus;
import eu.dreamlabs.videouploader.io.entity.VideoMetadataEntity;
import eu.dreamlabs.videouploader.io.repository.VideoMetadataRepository;
import eu.dreamlabs.videouploader.services.VideoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class VideoServiceImpl
        implements VideoService {
    private final VideoMetadataRepository videoMetadataRepository;
    private final RabbitMQUtil rabbitMQUtil;
    private final Environment environment;
    private final Map<String, Sinks.Many<VideoStatus>> statusStreams;
    private String uploadDir;

    public VideoServiceImpl(
            VideoMetadataRepository videoMetadataRepository,
            RabbitMQUtil rabbitMQUtil,
            Environment environment) {
        this.videoMetadataRepository = videoMetadataRepository;
        this.rabbitMQUtil = rabbitMQUtil;
        this.environment = environment;
        this.uploadDir = environment.getProperty("app.storage.local.base-dir");
        this.statusStreams = new ConcurrentHashMap<>();

    }

    @Override
    public Mono<VideoMetadataEntity> uploadVideo(String uploaderId, FilePart filePart) {
        Path storageDir = Paths.get(uploadDir).toAbsolutePath();
        String uniqueFilename = UUID.randomUUID() + "-" + filePart.filename();
        Path targetPath = storageDir.resolve(uniqueFilename);

        // Step 1: Ensure storage directory exists (blocking wrapped in boundedElastic)
        Mono<Path> ensureDir = Mono.fromCallable(() -> {
                    Files.createDirectories(storageDir);
                    return targetPath;
                })
                .subscribeOn(Schedulers.boundedElastic());

        // Step 2: Transfer file to disk reactively
        Mono<Path> saveFile = ensureDir.flatMap(path -> filePart.transferTo(path).thenReturn(path));

        // Step 3: Chain extraction of video resolution and file size after file is fully saved
        return saveFile.flatMap(path ->
                Mono.fromCallable(() -> Files.size(path)) // Get file size
                        .subscribeOn(Schedulers.boundedElastic())
                        .flatMap(fileSize ->
                                extractResolution(path)
                                        .flatMap(resolution -> {
                                            // Step 4: Build VideoMetadataEntity and save to MongoDB
                                            VideoMetadataEntity videoMetadata = new VideoMetadataEntity(
                                                    filePart.filename(),
                                                    "application/octet-stream",
                                                    fileSize,                  // <--- actual file size
                                                    path.toUri().toString(),
                                                    resolution,
                                                    uploaderId
                                            );

                                            return videoMetadataRepository.save(videoMetadata)
                                                    .doOnSuccess(rabbitMQUtil::publishVideoConversionRequest);
                                        })
                        )
                )
                .doOnSuccess(saved -> {
                    rabbitMQUtil.publishVideoConversionRequest(saved);
                    emitStatus(saved.getId(), VideoStatus.UPLOAD_COMPLETE);
                })
                .doOnError(e -> log.error("Failed to upload video {}", filePart.filename(), e));
    }

    /**
     * Extracts the video resolution using FFprobe (blocking, runs on boundedElastic)
     */
    private Mono<VideoResolution> extractResolution(Path videoPath) {
        return Mono.fromCallable(() -> {
            Path ffProbeExe = Paths.get(Objects.requireNonNull(environment.getProperty("app.ffmpeg.path")))
                    .toAbsolutePath();
            FFprobe ffprobe = new FFprobe(ffProbeExe);
            FFprobeResult result = ffprobe.setInput(videoPath.toAbsolutePath().toString())
                    .setShowStreams(true)
                    .setShowFormat(true)
                    .execute();

            System.out.println("stream size " + result.getStreams().size());
            System.out.println("filename " + result.getFormat().getFilename());

            Stream videoStream = result.getStreams().stream()
                    .filter(s -> s.getCodecType() == StreamType.VIDEO)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No video stream found"));

            int height = videoStream.getHeight();

            if (height >= 4320) return VideoResolution.RESOLUTION_4320P;
            else if (height >= 2160) return VideoResolution.RESOLUTION_2160P;
            else if (height >= 1440) return VideoResolution.RESOLUTION_1440P;
            else if (height >= 1080) return VideoResolution.RESOLUTION_1080P;
            else if (height >= 720) return VideoResolution.RESOLUTION_720P;
            else if (height >= 480) return VideoResolution.RESOLUTION_480P;
            else if (height >= 360) return VideoResolution.RESOLUTION_360P;
            else return VideoResolution.RESOLUTION_240P;

        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<VideoMetadataEntity> getVideoMetadata(
            String videoId) {
        return videoMetadataRepository.findById(videoId);
    }

    @Override
    public Mono<List<VideoMetadataEntity>> getAllVideos() {
        return videoMetadataRepository.findAll()
                .collectList();
    }

    /**
     * Get or create the event stream for a videoId
     */
    @Override
    public Flux<VideoStatus> streamStatus(String videoId) {
        Sinks.Many<VideoStatus> sink = statusStreams.computeIfAbsent(videoId,
                id -> Sinks.many().multicast().onBackpressureBuffer());
        return sink.asFlux();
    }

    /**
     * Emit a status update for a video
     */
    @Override
    public void emitStatus(String videoId, VideoStatus status) {
        Sinks.Many<VideoStatus> sink = statusStreams.get(videoId);
        if (sink != null) {
            log.info("📡 Emitting status {} for videoId={}", status, videoId);
            sink.tryEmitNext(status);
        } else {
            log.warn("⚠️ No active subscribers for videoId={}, skipping status {}", videoId, status);
        }
    }
}
