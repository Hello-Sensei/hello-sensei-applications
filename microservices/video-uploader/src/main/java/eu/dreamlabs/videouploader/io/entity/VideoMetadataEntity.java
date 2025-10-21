package eu.dreamlabs.videouploader.io.entity;

import eu.dreamlabs.videouploader.domain.ConvertedVideo;
import eu.dreamlabs.videouploader.domain.enums.VideoResolution;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;


@Getter @Setter
@NoArgsConstructor
@Document(collection = "videos_metadatas")
public class VideoMetadataEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private String id;
    private String originalFilename;
    private String contentType;
    private long size;
    private String storagePath; // e.g. s3://bucket/..., or file:/data/videos/...
    private Instant uploadedAt;
    private VideoResolution resolution;
    private String uploaderId; // optional: user id
    private List<ConvertedVideo> convertedPaths; // <-- NEW


    public VideoMetadataEntity(
            String originalFilename,
            String contentType,
            long size,
            String storagePath,
            VideoResolution resolution,
            String uploaderId) {
        this.originalFilename = originalFilename;
        this.contentType = contentType;
        this.size = size;
        this.storagePath = storagePath;
        this.resolution = resolution;
        this.uploaderId = uploaderId;
    }
}
