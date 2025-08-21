package com.cctv.controlcenter.api;

import com.cctv.controlcenter.domain.Camera;
import com.cctv.controlcenter.service.CameraService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cameras")
public class CameraController {
    
    private static final Logger log = LoggerFactory.getLogger(CameraController.class);
    
    private final CameraService cameraService;
    
    public CameraController(CameraService cameraService) {
        this.cameraService = cameraService;
    }
    
    @GetMapping
    public ResponseEntity<List<Camera>> getCameras() {
        // TODO: 실제 사용자 ID를 보안 컨텍스트에서 가져와야 함
        UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440001"); // data.sql의 사용자 ID
        
        log.info("사용자 {}의 카메라 목록 조회", userId);
        List<Camera> cameras = cameraService.getCamerasByUserId(userId);
        return ResponseEntity.ok(cameras);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Camera> getCamera(@PathVariable String id) {
        // TODO: 실제 사용자 ID를 보안 컨텍스트에서 가져와야 함
        UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440001"); // data.sql의 사용자 ID
        
        log.info("카메라 {} 상세 조회 (사용자: {})", id, userId);
        Camera camera = cameraService.getCameraById(id, userId);
        return ResponseEntity.ok(camera);
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<Camera> updateCameraStatus(@PathVariable String id, @RequestParam String status) {
        // TODO: 실제 사용자 ID를 보안 컨텍스트에서 가져와야 함
        UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440001"); // data.sql의 사용자 ID
        
        log.info("카메라 {} 상태 변경 요청: {} (사용자: {})", id, status, userId);
        
        try {
            Camera.CameraStatus newStatus = Camera.CameraStatus.valueOf(status.toUpperCase());
            Camera updatedCamera = cameraService.updateCameraStatus(id, newStatus, userId);
            return ResponseEntity.ok(updatedCamera);
        } catch (IllegalArgumentException e) {
            log.error("잘못된 카메라 상태: {}", status);
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping
    public ResponseEntity<Camera> createCamera(@Valid @RequestBody CameraCreateRequest request) {
        // TODO: 실제 사용자 ID를 보안 컨텍스트에서 가져와야 함
        UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440001"); // data.sql의 사용자 ID
        
        log.info("카메라 생성 요청: {} (사용자: {})", request.getName(), userId);
        
        try {
            Camera newCamera = cameraService.createCamera(request, userId);
            log.info("카메라 생성 완료: {}", newCamera.getId());
            return ResponseEntity.ok(newCamera);
        } catch (Exception e) {
            log.error("카메라 생성 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Camera> updateCamera(@PathVariable String id, @Valid @RequestBody CameraUpdateRequest request) {
        // TODO: 실제 사용자 ID를 보안 컨텍스트에서 가져와야 함
        UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440001"); // data.sql의 사용자 ID
        
        log.info("카메라 {} 수정 요청 (사용자: {})", id, userId);
        
        try {
            Camera updatedCamera = cameraService.updateCamera(id, request, userId);
            log.info("카메라 수정 완료: {}", id);
            return ResponseEntity.ok(updatedCamera);
        } catch (Exception e) {
            log.error("카메라 수정 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCamera(@PathVariable String id) {
        // TODO: 실제 사용자 ID를 보안 컨텍스트에서 가져와야 함
        UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440001"); // data.sql의 사용자 ID
        
        log.info("카메라 {} 삭제 요청 (사용자: {})", id, userId);
        
        try {
            cameraService.deleteCamera(id, userId);
            log.info("카메라 삭제 완료: {}", id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("카메라 삭제 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    // DTO 클래스들
    public static class CameraCreateRequest {
        private String name;
        private double lat;
        private double lng;
        private String rtspUrl;
        private String description;
        private boolean yoloEnabled = false; // 기본값: YOLO 비활성화
        
        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public double getLat() { return lat; }
        public void setLat(double lat) { this.lat = lat; }
        public double getLng() { return lng; }
        public void setLng(double lng) { this.lng = lng; }
        public String getRtspUrl() { return rtspUrl; }
        public void setRtspUrl(String rtspUrl) { this.rtspUrl = rtspUrl; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public boolean isYoloEnabled() { return yoloEnabled; }
        public void setYoloEnabled(boolean yoloEnabled) { this.yoloEnabled = yoloEnabled; }
    }
    
    public static class CameraUpdateRequest {
        private String name;
        private double lat;
        private double lng;
        private String rtspUrl;
        private String description;
        private boolean yoloEnabled;
        
        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public double getLat() { return lat; }
        public void setLat(double lat) { this.lat = lat; }
        public double getLng() { return lng; }
        public void setLng(double lng) { this.lng = lng; }
        public String getRtspUrl() { return rtspUrl; }
        public void setRtspUrl(String rtspUrl) { this.rtspUrl = rtspUrl; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public boolean isYoloEnabled() { return yoloEnabled; }
        public void setYoloEnabled(boolean yoloEnabled) { this.yoloEnabled = yoloEnabled; }
    }
}
