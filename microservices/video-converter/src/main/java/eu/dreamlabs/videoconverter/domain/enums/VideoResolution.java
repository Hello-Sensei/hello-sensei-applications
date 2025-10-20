package eu.dreamlabs.videoconverter.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum VideoResolution {
    RESOLUTION_4320P("4320p (8K)", 7680, 4320),
    RESOLUTION_2160P("2160p (4K)", 3840, 2160),
    RESOLUTION_1440P("1440p (2K)", 2560, 1440),
    RESOLUTION_1080P("1080p (HD)", 1920, 1080),
    RESOLUTION_720P("720p (HD)", 1280, 720),
    RESOLUTION_480P("480p (SD)", 854, 480),
    RESOLUTION_360P("360p (SD)", 640, 360),
    RESOLUTION_240P("240p (SD)", 426, 240);

    private final String label;
    private final int width;
    private final int height;

    public static VideoResolution fromLabel(String label) {
        for (VideoResolution res : values()) {
            if (res.label.equalsIgnoreCase(label)) {
                return res;
            }
        }
        throw new IllegalArgumentException("Unknown resolution: " + label);
    }
}
