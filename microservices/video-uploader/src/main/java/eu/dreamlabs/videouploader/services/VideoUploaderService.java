package eu.dreamlabs.videouploader.services;

import eu.dreamlabs.videouploader.domain.enums.VideoResolution;
import eu.dreamlabs.videouploader.io.entity.VideoMetadataEntity;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface VideoUploaderService {
    Mono<VideoMetadataEntity> uploadVideo(
            String uploaderId,
            FilePart filePart) throws IOException;

    public Mono<VideoMetadataEntity> getVideoMetadata(
            String videoId);

    public Mono<List<VideoMetadataEntity>> getAllVideos();

}
