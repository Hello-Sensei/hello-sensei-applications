package eu.dreamlabs.videoconverter.messaging;

import eu.dreamlabs.videoconverter.io.entity.VideoMetadataEntity;
import eu.dreamlabs.videoconverter.services.VideoConversionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class VideoUploadListener {
    private final VideoConversionService conversionService;

    @RabbitListener(
            queues = "${app.rabbit.queues.upload}",  // ← use the upload queue from YAML
            ackMode = "AUTO"
    )
    public void handleVideoUploaded(VideoMetadataEntity videoMetadata) {
        log.info("Received video for conversion: {}", videoMetadata.getOriginalFilename());
        conversionService.convertVideo(videoMetadata)
                .subscribe(
                        result -> log.info("Conversion finished for: {}", result.getOriginalFilename()),
                        error -> log.error("Conversion failed for: {}", videoMetadata.getOriginalFilename(), error));

                //.doOnError(e -> log.error("Conversion failed for videoId={}", videoMetadata.getId(), e))
                //.subscribe();
    }
}
