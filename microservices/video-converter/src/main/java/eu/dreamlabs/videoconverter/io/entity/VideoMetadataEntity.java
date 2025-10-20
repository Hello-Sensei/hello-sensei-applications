package eu.dreamlabs.videoconverter.io.entity;

import eu.dreamlabs.videoconverter.domain.ConvertedVideo;
import eu.dreamlabs.videoconverter.domain.enums.VideoResolution;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "videos_metadatas")
public class VideoMetadataEntity {
    @Id
    private String id;
    private String originalFilename;
    private String contentType;
    private long size;
    private String storagePath;
    private Instant uploadedAt;
    private VideoResolution resolution;
    private String uploaderId;
    private List<ConvertedVideo> convertedPaths; // <-- NEW
}