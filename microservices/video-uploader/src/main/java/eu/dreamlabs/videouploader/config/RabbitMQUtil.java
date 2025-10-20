package eu.dreamlabs.videouploader.config;

import eu.dreamlabs.videouploader.io.entity.VideoMetadataEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitMQUtil {

    private final AmqpTemplate amqpTemplate;
    private final Environment environment;

    public void publishVideoConversionRequest(VideoMetadataEntity videoMetadata) {
        String exchange = environment.getProperty("app.rabbit.exchange.upload");
        String routingKey = environment.getProperty("app.rabbit.routingKeys.upload");

        try {
            amqpTemplate.convertAndSend(exchange, routingKey, videoMetadata);
            log.info("Published video conversion request: {}", videoMetadata.getOriginalFilename());
        } catch (Exception e) {
            log.error("Failed to publish video conversion request", e);
        }
    }
}
