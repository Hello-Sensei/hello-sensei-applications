package eu.dreamlabs.videouploader.controllers;

import eu.dreamlabs.videouploader.io.entity.VideoMetadataEntity;
import eu.dreamlabs.videouploader.services.VideoUploaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@Validated
@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
public class VideosController {
    private final VideoUploaderService videoUploaderService;

    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<VideoMetadataEntity>> uploadVideo(
            @RequestPart("file") FilePart file,
            @RequestPart(value = "uploaderId", required = false) String uploaderId)
            throws IOException {
        return videoUploaderService.uploadVideo(uploaderId, file)
                .map(saved -> ResponseEntity.status(HttpStatus.CREATED).body(saved))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<VideoMetadataEntity>> getVideoMetadata(
            @PathVariable String id) {
        return videoUploaderService.getVideoMetadata(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
