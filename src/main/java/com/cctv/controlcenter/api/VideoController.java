package com.cctv.controlcenter.api;

import com.cctv.controlcenter.api.dto.VideoCreateRequest;
import com.cctv.controlcenter.domain.Video;
import com.cctv.controlcenter.service.VideoService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/videos")
public class VideoController {
    
    private static final Logger log = LoggerFactory.getLogger(VideoController.class);
    
    private final VideoService videoService;
    
    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }
    
    @PostMapping
    public ResponseEntity<Video> createVideo(@Valid @RequestBody VideoCreateRequest request) {
        log.info("비디오 생성 요청: {}", request);
        Video video = videoService.createVideo(request);
        return ResponseEntity.ok(video);
    }
    
    @GetMapping
    public ResponseEntity<List<Video>> getVideos(
            @RequestParam String cameraId,
            @RequestParam(required = false) LocalDateTime from,
            @RequestParam(required = false) LocalDateTime to) {
        
        log.info("비디오 목록 조회: cameraId={}, from={}, to={}", cameraId, from, to);
        List<Video> videos = videoService.getVideosByCamera(cameraId, from, to);
        return ResponseEntity.ok(videos);
    }
    
    @GetMapping("/{id}/stream")
    public ResponseEntity<byte[]> streamVideo(
            @PathVariable UUID id,
            @RequestHeader(value = "Range", required = false) String range) {
        
        log.info("비디오 스트림 요청: id={}, range={}", id, range);
        
        try {
            long start = 0;
            long end = -1;
            
            if (range != null && range.startsWith("bytes=")) {
                String[] ranges = range.substring(6).split("-");
                start = Long.parseLong(ranges[0]);
                if (ranges.length > 1 && !ranges[1].isEmpty()) {
                    end = Long.parseLong(ranges[1]);
                }
            }
            
            byte[] videoData = videoService.getVideoStream(id, start, end);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentLength(videoData.length);
            
            if (range != null) {
                headers.set("Accept-Ranges", "bytes");
                headers.set("Content-Range", "bytes " + start + "-" + (end > 0 ? end : videoData.length - 1) + "/" + videoData.length);
            }
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(videoData);
                    
        } catch (IOException e) {
            log.error("비디오 스트리밍 실패: {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
