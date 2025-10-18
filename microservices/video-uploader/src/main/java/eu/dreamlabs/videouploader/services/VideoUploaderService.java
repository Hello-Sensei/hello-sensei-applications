package eu.dreamlabs.videouploader.services;

import eu.dreamlabs.videouploader.io.entity.VideoMetadataEntity;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

public interface VideoUploaderService {
    Mono<VideoMetadataEntity> upload(
            FilePart filePart,
            String uploaderId);
}
