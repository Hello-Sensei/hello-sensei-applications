package eu.dreamlabs.videouploader.controllers;

import eu.dreamlabs.videouploader.io.entity.VideoMetadataEntity;
import eu.dreamlabs.videouploader.services.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
public class VideosController {
    private final VideoService videoService;

    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<VideoMetadataEntity>> uploadVideo(
            @RequestPart("file") FilePart file,
            @RequestPart(value = "uploaderId", required = false) String uploaderId)
            throws IOException {
        return videoService.uploadVideo(uploaderId, file)
                .map(saved -> ResponseEntity.status(HttpStatus.CREATED).body(saved))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()));
    }

    @GetMapping(value = "/status/{videoId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<Map<String, String>>> streamVideoStatus(@PathVariable String videoId) {
        return videoService.streamStatus(videoId)
                .map(status -> ServerSentEvent.<Map<String, String>>builder()
                        .event("message")
                        .data(Map.of("type", status.name()))
                        .build());
    }


    @GetMapping("/{id}")
    public Mono<ResponseEntity<VideoMetadataEntity>> getVideoMetadata(
            @PathVariable String id) {
        return videoService.getVideoMetadata(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
