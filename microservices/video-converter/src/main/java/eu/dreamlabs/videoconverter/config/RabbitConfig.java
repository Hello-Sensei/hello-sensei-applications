package eu.dreamlabs.videoconverter.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Value("${app.rabbit.exchange.upload}")
    private String uploadExchange;

    @Value("${app.rabbit.queues.upload}")
    private String uploadQueue;

    @Value("${app.rabbit.routingKeys.upload}")
    private String uploadRoutingKey;

    @Value("${app.rabbit.exchange.convert}")
    private String convertExchange;

    @Value("${app.rabbit.queues.convert}")
    private String convertQueue;

    @Value("${app.rabbit.routingKeys.convert}")
    private String convertRoutingKey;

    @Bean
    public Queue uploadQueue() {
        return new Queue(uploadQueue, true);
    }

    @Bean
    public Queue convertQueue() {
        return new Queue(convertQueue, true);
    }

    @Bean
    public DirectExchange uploadExchange() {
        return new DirectExchange(uploadExchange);
    }

    @Bean
    public DirectExchange convertExchange() {
        return new DirectExchange(convertExchange);
    }

    @Bean
    public Binding uploadBinding(Queue uploadQueue, DirectExchange uploadExchange) {
        return BindingBuilder.bind(uploadQueue).to(uploadExchange).with(uploadRoutingKey);
    }

    @Bean
    public Binding convertBinding(Queue convertQueue, DirectExchange convertExchange) {
        return BindingBuilder.bind(convertQueue).to(convertExchange).with(convertRoutingKey);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        // ADDED: JSON converter for receiving VideoMetadataEntity
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        return template;
    }
}