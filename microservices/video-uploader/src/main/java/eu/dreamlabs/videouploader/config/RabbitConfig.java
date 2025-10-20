package eu.dreamlabs.videouploader.config;


//import org.springframework.amqp.core.*;
//import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
//import org. springframework. amqp. rabbit. connection. ConnectionFactory;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
//import org.springframework.amqp.support.converter.MessageConverter;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        return template;
    }

//    @Value("${app.rabbit.exchange.upload}")
//    private String uploadExchange;
//
//    @Value("${app.rabbit.exchange.convert}")
//    private String convertedExchange;
//
//    @Value("${app.rabbit.queues.upload}")
//    private String uploadedQueue;
//
//    @Value("${app.rabbit.queues.convert}")
//    private String convertedQueue;
//
//    @Value("${app.rabbit.routingKeys.upload}")
//    private String uploadRoutingKey;
//
//    @Value("${app.rabbit.routingKeys.convert}")
//    private String convertRoutingKey;
//
//    @Bean
//    public DirectExchange videoExchange() {
//        return new DirectExchange(uploadExchange);
//    }
//
//    @Bean
//    public DirectExchange videoConvertedExchange() {
//        return new DirectExchange(convertedExchange);
//    }
//
//    @Bean
//    public Queue videoUploadedQueue() {
//        return QueueBuilder
//                .durable(uploadedQueue)
//                .build();
//    }
//
//    @Bean
//    public Queue videoConvertedQueue() {
//        return QueueBuilder
//                .durable(convertedQueue)
//                .build();
//    }
//
//    @Bean
//    public Binding bindingUpload(Queue videoUploadedQueue, DirectExchange videoExchange) {
//        return BindingBuilder
//                .bind(videoUploadedQueue)
//                .to(videoExchange)
//                .with(uploadRoutingKey);
//    }
//
//    @Bean
//    public Binding bindingConverted(Queue videoConvertedQueue, DirectExchange videoConvertedExchange) {
//        return BindingBuilder
//                .bind(videoConvertedQueue)
//                .to(videoConvertedExchange)
//                .with(convertRoutingKey);
//    }
//
//    @Bean
//    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
//        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
//        factory.setConnectionFactory(connectionFactory);
//        factory.setMessageConverter(new Jackson2JsonMessageConverter()); // use JSON converter
//        factory.setConcurrentConsumers(3);    // optional tuning
//        factory.setMaxConcurrentConsumers(10);
//        return factory;
//    }
//
//    @Bean
//    public MessageConverter jsonMessageConverter() {
//        return new Jackson2JsonMessageConverter();
//    }
//
//    @Bean
//    public RabbitTemplate rabbitTemplate(ConnectionFactory cf) {
//        RabbitTemplate rt = new RabbitTemplate(cf);
//        rt.setMessageConverter(jsonMessageConverter());
//        return rt;
//    }
}
