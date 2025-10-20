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

    @Override
    public Mono<VideoMetadataEntity> convertVideo(VideoMetadataEntity videoMetadata) {
        Path ffmpegPath = Paths.get(Objects.requireNonNull(environment.getProperty("app.ffmpeg.path"))).toAbsolutePath();
        Path outputDir = Paths.get(Objects.requireNonNull(environment.getProperty("app.ffmpeg.output-dir"))).toAbsolutePath();

        return Mono.fromCallable(() -> {
                    Files.createDirectories(outputDir);
                    // Define target resolutions
                    VideoResolution[] targetResolutions = {
                            VideoResolution.RESOLUTION_1080P,
                            VideoResolution.RESOLUTION_720P,
                            VideoResolution.RESOLUTION_480P
                    };
                    List<ConvertedVideo> convertedVideos = new ArrayList<>();

                    for (VideoResolution res : targetResolutions) {
                        Path outputFile = outputDir.resolve(res.name() + "-" + videoMetadata.getOriginalFilename());
                        FFmpeg ffmpeg = new FFmpeg(ffmpegPath);
                        FFmpegResult result = ffmpeg
                                .addInput(UrlInput.fromPath(Paths.get(new URI(videoMetadata.getStoragePath()))))
                                .addOutput(UrlOutput.toPath(outputFile)
                                .setCodec(StreamType.VIDEO, "libx264")
                                .setCodec(StreamType.AUDIO, "aac")
                                .setFrameSize(res.getWidth(), res.getHeight()))
                                .execute();

                        System.out.println("Video size " + result.getVideoSize());
                        System.out.println("Audio size " + result.getAudioSize());

                        ConvertedVideo convertedVideo = new ConvertedVideo(
                                "libx264",
                                "aac",
                                res,
                                outputFile.toUri().toString()
                        );
                        convertedVideos.add(convertedVideo);
                    }

                    videoMetadata.setConvertedPaths(convertedVideos);
                    return videoMetadata;
                }).flatMap(convertedRepository::save)
                .subscribeOn(Schedulers.boundedElastic());
    }
}
