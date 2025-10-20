package eu.dreamlabs.videouploader.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

//@Component
//@Slf4j
//@RequiredArgsConstructor
//public class ConversionResultListener {
//    private final ConversionEventSink sink;
//
//    @RabbitListener(queues = "video-converted-queue")
//    public void handleVideoConverted(Map<String, Object> event) {
//        log.info("Received converted event: {}", event);
//        sink.emit(event);
//    }
//}
