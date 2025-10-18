package eu.dreamlabs.videouploader.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.dreamlabs.videouploader.services.RabbitMqEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class RabbitMqEventPublisherImpl implements RabbitMqEventPublisher {
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    @Value("${spring.rabbitmq.template.exchange}")
    private String exchange;
    @Value("${spring.rabbitmq.template.routingKey}")
    private String routingKey;

    @Override
    public Mono<Void> publishUploadEvent(
            Map<String, Object> payload) {
        // convert to JSON then send using boundedElastic to avoid blocking on RabbitTemplate
        return Mono
                .fromCallable(() -> {
                    String json = objectMapper.writeValueAsString(payload);
                    rabbitTemplate.convertAndSend(exchange, routingKey, json);
                    return true;
                })
                .subscribeOn(Schedulers.boundedElastic()).then();
    }
}