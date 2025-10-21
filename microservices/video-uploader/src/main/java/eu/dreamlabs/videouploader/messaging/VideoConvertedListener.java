package eu.dreamlabs.videouploader.messaging;

import eu.dreamlabs.videouploader.io.entity.VideoMetadataEntity;
import eu.dreamlabs.videouploader.io.repository.VideoMetadataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class VideoConvertedListener {
    private final VideoMetadataRepository videoMetadataRepository;

    @RabbitListener(
            queues = "${app.rabbit.queues.convert}",
            ackMode = "AUTO"
    )
    public void handleVideoConverted(VideoMetadataEntity convertedVideo) {
        log.info("🎬 Received converted video: {}", convertedVideo.getOriginalFilename());

        // ✅ Instead of overwriting, update only convertedPaths
        videoMetadataRepository.findById(convertedVideo.getId())
                .flatMap(existing -> {
                    existing.setConvertedPaths(convertedVideo.getConvertedPaths());
                    return videoMetadataRepository.save(existing);
                })
                .doOnSuccess(v -> log.info("✅ Updated convertedPaths for videoId={}", v.getId()))
                .doOnError(e -> log.error("❌ Failed to update convertedPaths", e))
                .subscribe();
    }
//
//    @RabbitListener(queues = "${app.rabbit.queues.convert}")
//    public void handleConvertedVideo(VideoMetadataEntity converted) {
//        log.info("Received converted video: {}", converted.getOriginalFilename());
//    }
}
