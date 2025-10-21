package eu.dreamlabs.videouploader.services;

import eu.dreamlabs.videouploader.domain.enums.VideoStatus;
import eu.dreamlabs.videouploader.io.entity.VideoMetadataEntity;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;

public interface VideoService {
    Mono<VideoMetadataEntity> uploadVideo(
            String uploaderId,
            FilePart filePart) throws IOException;

    Mono<VideoMetadataEntity> getVideoMetadata(
            String videoId);

    Mono<List<VideoMetadataEntity>> getAllVideos();

    Flux<VideoStatus> streamStatus(String videoId);
    void emitStatus(String videoId, VideoStatus status);
}
