package eu.dreamlabs.videouploader.services.impl;

import eu.dreamlabs.videouploader.services.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Slf4j
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {
    @Value("${app.storage.local.base-dir}")
    private String baseDir;

    @Override
    public Mono<String> store(FilePart filePart, String generatedFilename) {
        Path destination = Path.of(baseDir).resolve(generatedFilename);

        return Mono.fromCallable(() -> {
                    // Ensure parent directories exist
                    Files.createDirectories(destination.getParent());
                    return destination;
                })
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(dest ->
                        // Use transferTo (reactive) on boundedElastic to avoid blocking Netty
                        filePart.transferTo(dest)
                                .thenReturn(dest.toUri().toString())
                                .subscribeOn(Schedulers.boundedElastic())
                )
                .doOnSubscribe(sub -> log.info("📦 Storing file to {}", destination))
                .doOnSuccess(uri -> log.info("✅ File stored at {}", uri))
                .doOnError(err -> log.error("❌ Failed to store file: {}", err.getMessage(), err));
    }
}
