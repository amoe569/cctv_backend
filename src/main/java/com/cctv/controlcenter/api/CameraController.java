package com.cctv.controlcenter.api;

import com.cctv.controlcenter.domain.Camera;
import com.cctv.controlcenter.service.CameraService;
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
}
