package eu.dreamlabs.videouploader.io.repository;

import eu.dreamlabs.videouploader.io.entity.VideoMetadataEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface VideoMetadataRepository extends
        ReactiveCrudRepository<VideoMetadataEntity, String> {
    Flux<VideoMetadataEntity> findByUploaderId(String uploaderId);
}
