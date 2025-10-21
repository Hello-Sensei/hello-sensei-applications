package eu.dreamlabs.videoconverter.services.impl;

import com.github.kokorin.jaffree.StreamType;
import com.github.kokorin.jaffree.ffmpeg.FFmpeg;
import com.github.kokorin.jaffree.ffmpeg.FFmpegResult;
import com.github.kokorin.jaffree.ffmpeg.UrlInput;
import com.github.kokorin.jaffree.ffmpeg.UrlOutput;
import eu.dreamlabs.videoconverter.domain.ConvertedVideo;
import eu.dreamlabs.videoconverter.domain.enums.VideoResolution;
import eu.dreamlabs.videoconverter.io.entity.VideoMetadataEntity;
import eu.dreamlabs.videoconverter.io.repository.VideoConvertedRepository;
import eu.dreamlabs.videoconverter.services.VideoConversionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoConversionServiceImpl implements VideoConversionService {

    private final Environment environment;
    private final VideoConvertedRepository convertedRepository;
    private final AmqpTemplate amqpTemplate;

    @Override
    public Mono<VideoMetadataEntity> convertVideo(VideoMetadataEntity videoMetadata) {
        Path ffmpegPath = Paths.get(Objects.requireNonNull(environment.getProperty("app.ffmpeg.path"))).toAbsolutePath();
        Path outputDir = Paths.get(Objects.requireNonNull(environment.getProperty("app.ffmpeg.output-dir"))).toAbsolutePath();

        return Mono.fromCallable(() -> {
                    // --- ensure output directory exists (blocking) ---
                    Files.createDirectories(outputDir);
                    // Define target resolutions
                    // --- Resolve original file path & filename from storagePath URI ---
                    // videoMetadata.getStoragePath() is like "file:///D:/.../uuid-name.mp4"
                    Path sourcePath = Paths.get(new URI(videoMetadata.getStoragePath()));
                    String originalFileName = sourcePath.getFileName().toString();

                    // --- Determine original resolution height ---
                    VideoResolution originalResolution = videoMetadata.getResolution();
                    int originalHeight = (originalResolution != null) ? originalResolution.getHeight() : Integer.MAX_VALUE;

                    // --- Build list of target resolutions: include all VideoResolution values
                    //     whose height is <= originalHeight. VideoResolution.values() are declared
                    //     in descending order, so iteration preserves descending order (original -> lower).
                    List<VideoResolution> targetResolutions = new ArrayList<>();
                    for (VideoResolution vr : VideoResolution.values()) {
                        if (vr.getHeight() <= originalHeight) {
                            targetResolutions.add(vr);
                        }
                    }
                    List<ConvertedVideo> convertedVideos = new ArrayList<>();
                    for (VideoResolution res : targetResolutions) {
                        // output file name pattern: RESOLUTION_1080P-<originalFileName>


                        Path outputFile = outputDir.resolve(
                                res.name().toLowerCase() + new File(videoMetadata.getStoragePath()).getName());
                        // outputDir
                        // Run ffmpeg conversion (blocking call handled by boundedElastic via subscribeOn)
                        FFmpeg ffmpeg = new FFmpeg(ffmpegPath);
                        FFmpegResult result = ffmpeg
                                .addInput(UrlInput.fromPath(Paths.get(new URI(videoMetadata.getStoragePath()))))
                                .addOutput(UrlOutput.toPath(outputFile)
                                        .setCodec(StreamType.VIDEO, res.getVCodec())
                                        .setCodec(StreamType.AUDIO, res.getACodec())
                                        .addArguments("-b:v", res.getVBitrate())
                                        .addArguments("-b:a", res.getABitrate())
                                        .addArguments("-preset", res.getPreset())
                                        .setFrameSize(res.getWidth(), res.getHeight()))
                                .execute();

                        System.out.println("Video size " + result.getVideoSize());
                        System.out.println("Audio size " + result.getAudioSize());

                        // collect converted info
                        ConvertedVideo convertedVideo = new ConvertedVideo(
                                res.getVCodec(),
                                res.getACodec(),
                                res,
                                outputFile.toUri().toString()
                        );
                        convertedVideos.add(convertedVideo);
                        System.out.println("Converted to " + res.name() + " -> " + outputFile + " (videoSize=" + result.getVideoSize() + ")");
                    }
                    // Set convertedPaths on entity, preserve other fields
                    videoMetadata.setConvertedPaths(convertedVideos);
                    return videoMetadata;
                })
                .flatMap(convertedRepository::save)
                .doOnSuccess(saved -> {
                    // Publish async result to uploader
                    String exchange = environment.getProperty("app.rabbit.exchange.convert");
                    String routingKey = environment.getProperty("app.rabbit.routingKeys.convert");

                    amqpTemplate.convertAndSend(exchange, routingKey, saved);
                    log.info("✅ Sent converted video result back: {}", saved.getOriginalFilename());
                })
                .subscribeOn(Schedulers.boundedElastic());
    }
}
