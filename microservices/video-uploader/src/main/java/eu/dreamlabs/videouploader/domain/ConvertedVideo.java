package eu.dreamlabs.videouploader.domain;

import eu.dreamlabs.videouploader.domain.enums.VideoResolution;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConvertedVideo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String vCodec;
    private String aCodec;
    private VideoResolution resolution;
    private String storagePath;
}