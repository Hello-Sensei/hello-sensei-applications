package eu.dreamlabs.videouploader.services.impl;

import eu.dreamlabs.videouploader.io.entity.VideoMetadataEntity;
import eu.dreamlabs.videouploader.io.repository.VideoMetadataRepository;
import eu.dreamlabs.videouploader.services.RabbitMqEventPublisher;
import eu.dreamlabs.videouploader.services.StorageService;
import eu.dreamlabs.videouploader.services.VideoUploaderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class VideoUploaderServiceImpl
        implements VideoUploaderService {
    private final StorageService storageService;
    private final VideoMetadataRepository metadataRepository;
    private final RabbitMqEventPublisher eventPublisher;

    @Override
    public Mono<VideoMetadataEntity> upload(
            FilePart filePart,
            String uploaderId) {
        String generatedFilename = UUID.randomUUID() + "-" + filePart.filename();
        return storageService.store(filePart, generatedFilename)
                .flatMap(storagePath -> {
                    VideoMetadataEntity meta = VideoMetadataEntity.builder()
                            .originalFilename(filePart.filename())
                            .contentType(filePart.headers().getContentType() != null ?
                                    filePart.headers().getContentType().toString() :
                                    "application/octet-stream")
                            .size(0L)
                            .storagePath(storagePath)
                            .uploadedAt(Instant.now())
                            .uploaderId(uploaderId)
                            .build();

                    // Save it to Mongo (reactive)
                    return metadataRepository.save(meta)
                            .flatMap(saved -> {
                                Map<String, Object> event = new HashMap<>();
                                event.put("id", saved.getId());
                                event.put("originalFilename", saved.getOriginalFilename());
                                event.put("storagePath", saved.getStoragePath());
                                event.put("contentType", saved.getContentType());
                                event.put("size", saved.getSize());
                                event.put("uploadedAt", saved.getUploadedAt().toString());
                                event.put("uploaderId", saved.getUploaderId());
                                // publish event (non-blocking Mono)
                                return eventPublisher.publishUploadEvent(event)
                                        .thenReturn(saved);
                            });
                });
    }
}
