package eu.dreamlabs.videoconverter.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class RabbitMqEventPublisher {
    private final RabbitTemplate rabbitTemplate;
    @Value("${spring.rabbitmq.template.exchange}")
    private String exchange;
    @Value("${spring.rabbitmq.template.routingKey}")
    private String routingKey;

    /**
     * Publish a "video converted" event
     */
    public Mono<Void> publishVideoConvertedEvent(Map<String, Object> event) {
        return Mono.fromRunnable(() -> {
                    rabbitTemplate.convertAndSend(
                            exchange,    // exchange from your YAML
                            routingKey,  // routing key
                            event
                    );
                    log.info("Published video converted event for videoId={}", event.get("videoId"));
                })
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }
}