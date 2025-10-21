package eu.dreamlabs.videoconverter.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum VideoResolution {
    RESOLUTION_4320P("4320p (8K)", 7680, 4320, "libx265", "20M", "aac", "320k", "slower"),
    RESOLUTION_2160P("2160p (4K)", 3840, 2160, "libx265", "12M", "aac", "256k", "slow"),
    RESOLUTION_1440P("1440p (2K)", 2560, 1440, "libx264", "8M", "aac", "192k", "medium"),
    RESOLUTION_1080P("1080p (HD)", 1920, 1080, "libx264", "5M", "aac", "160k", "medium"),
    RESOLUTION_720P("720p (HD)", 1280, 720, "libx264", "3M", "aac", "128k", "faster"),
    RESOLUTION_480P("480p (SD)", 854, 480, "libx264", "1.5M", "aac", "96k", "fast"),
    RESOLUTION_360P("360p (SD)", 640, 360, "libx264", "1M", "aac", "64k", "veryfast"),
    RESOLUTION_240P("240p (SD)", 426, 240, "libx264", "700k", "aac", "64k", "veryfast");

    private final String label;
    private final int width;
    private final int height;
    private final String vCodec;
    private final String vBitrate;
    private final String aCodec;
    private final String aBitrate;
    private final String preset;

    public static VideoResolution fromLabel(String label) {
        for (VideoResolution res : values()) {
            if (res.label.equalsIgnoreCase(label)) {
                return res;
            }
        }
        throw new IllegalArgumentException("Unknown resolution: " + label);
    }
}
