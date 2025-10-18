package eu.dreamlabs.videouploader.config;

import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    @Value("${spring.rabbitmq.template.exchange}")
    private String exchange;

    @Bean
    public Exchange videoExchange() {
        return new TopicExchange(exchange);
    }
}
