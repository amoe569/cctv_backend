package com.cctv.controlcenter.service;

import com.cctv.controlcenter.api.dto.VideoCreateRequest;
import com.cctv.controlcenter.domain.Camera;
import com.cctv.controlcenter.domain.Video;
import com.cctv.controlcenter.repository.CameraRepository;
import com.cctv.controlcenter.repository.VideoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class VideoService {
    
    private static final Logger log = LoggerFactory.getLogger(VideoService.class);
    
    private final VideoRepository videoRepository;
    private final CameraRepository cameraRepository;
    
    public VideoService(VideoRepository videoRepository, CameraRepository cameraRepository) {
        this.videoRepository = videoRepository;
        this.cameraRepository = cameraRepository;
    }
    
    @Transactional
    public Video createVideo(VideoCreateRequest request) {
        log.info("비디오 생성 요청: cameraId={}, path={}", 
                request.getCameraId(), request.getPath());
        
        // 카메라 존재 여부 확인
        Camera camera = cameraRepository.findById(request.getCameraId())
                .orElseThrow(() -> new IllegalArgumentException("카메라를 찾을 수 없습니다: " + request.getCameraId()));
        
        // 비디오 생성
        Video video = new Video();
        video.setId(UUID.randomUUID());
        video.setCamera(camera);
        video.setStartTs(request.getStartTs());
        video.setEndTs(request.getEndTs());
        video.setPath(request.getPath());
        video.setFileSizeBytes(request.getFileSizeBytes());
        video.setChecksum(request.getChecksum());
        video.setCodec(request.getCodec());
        
        // 지속 시간 계산
        if (request.getStartTs() != null && request.getEndTs() != null) {
            Duration duration = Duration.between(request.getStartTs(), request.getEndTs());
            video.setDurationSec((int) duration.getSeconds());
        }
        
        Video savedVideo = videoRepository.save(video);
        log.info("비디오 생성 완료: id={}, duration={}초", savedVideo.getId(), savedVideo.getDurationSec());
        
        return savedVideo;
    }
    
    public List<Video> getVideosByCamera(String cameraId, LocalDateTime from, LocalDateTime to) {
        if (from != null && to != null) {
            // 시간 범위가 지정된 경우
            Pageable pageable = PageRequest.of(0, 100); // 최대 100개
            Page<Video> page = videoRepository.findByCameraIdAndTimeRange(cameraId, from, to, pageable);
            return page.getContent();
        } else {
            // 시간 범위가 지정되지 않은 경우
            return videoRepository.findByCameraId(cameraId);
        }
    }
    
    public Video getVideoById(UUID videoId) {
        return videoRepository.findById(videoId)
                .orElseThrow(() -> new IllegalArgumentException("비디오를 찾을 수 없습니다: " + videoId));
    }
    
    public byte[] getVideoStream(UUID videoId, long start, long end) throws IOException {
        Video video = getVideoById(videoId);
        String filePath = video.getPath();
        
        log.info("비디오 스트림 요청: id={}, path={}, range={}-{}", 
                videoId, filePath, start, end);
        
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            log.warn("비디오 파일이 존재하지 않음: {}", filePath);
            throw new IOException("비디오 파일을 찾을 수 없습니다: " + filePath);
        }
        
        try {
            byte[] data = Files.readAllBytes(path);
            log.info("비디오 파일 읽기 완료: {} 바이트", data.length);
            
            if (end > 0 && end < data.length) {
                int length = (int) (end - start + 1);
                byte[] result = new byte[length];
                System.arraycopy(data, (int) start, result, 0, length);
                return result;
            }
            
            return data;
        } catch (IOException e) {
            log.error("비디오 파일 읽기 실패: {}", filePath, e);
            throw e;
        }
    }
}
