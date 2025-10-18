package eu.dreamlabs.videouploader.services;

import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

public interface StorageService {
    Mono<String> store(FilePart filePart, String generatedFilename);
}
