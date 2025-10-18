package eu.dreamlabs.videouploader.io.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Builder
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Document(collection = "videos_metadatas")
public class VideoMetadataEntity {
    @Id
    private String id;
    private String originalFilename;
    private String contentType;
    private long size;
    private String storagePath; // e.g. s3://bucket/..., or file:/data/videos/...
    private Instant uploadedAt;
    private String uploaderId; // optional: user id
}
