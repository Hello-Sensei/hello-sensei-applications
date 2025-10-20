package eu.dreamlabs.videouploader.messaging;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
//
//@Component
//public class ConversionEventSink {
//    private final Sinks.Many<Object> sink = Sinks.many().multicast().onBackpressureBuffer();
//
//    public void emit(Object event) {
//        sink.tryEmitNext(event);
//    }
//
//    public Flux<Object> getEvents() {
//        return sink.asFlux();
//    }
//}