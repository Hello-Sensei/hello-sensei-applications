package eu.dreamlabs.videoconverter.services;

import eu.dreamlabs.videoconverter.io.entity.VideoMetadataEntity;
import reactor.core.publisher.Mono;

public interface VideoConversionService {
    Mono<VideoMetadataEntity> convertVideo(
            VideoMetadataEntity videoMetadataEntity);
}
