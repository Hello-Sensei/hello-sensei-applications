package eu.dreamlabs.videoconverter.io.repository;

import eu.dreamlabs.videoconverter.io.entity.VideoMetadataEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface VideoConvertedRepository extends
        ReactiveCrudRepository<VideoMetadataEntity, String> {
    Mono<VideoMetadataEntity> findById(String videoId);
}
