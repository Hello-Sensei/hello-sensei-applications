package eu.dreamlabs.videouploader.services;

import reactor.core.publisher.Mono;

import java.util.Map;

public interface RabbitMqEventPublisher {
    Mono<Void> publishUploadEvent(
            Map<String, Object> payload);
}
