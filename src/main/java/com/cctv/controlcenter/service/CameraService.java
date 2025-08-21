package com.cctv.controlcenter.service;

import com.cctv.controlcenter.api.CameraController;
import com.cctv.controlcenter.domain.Camera;
import com.cctv.controlcenter.domain.User;
import com.cctv.controlcenter.repository.CameraRepository;
import com.cctv.controlcenter.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CameraService {
    
    private static final Logger log = LoggerFactory.getLogger(CameraService.class);
    
    private final CameraRepository cameraRepository;
    private final UserRepository userRepository;
    
    public CameraService(CameraRepository cameraRepository, UserRepository userRepository) {
        this.cameraRepository = cameraRepository;
        this.userRepository = userRepository;
    }
    
    public List<Camera> getCamerasByUserId(UUID userId) {
        log.info("사용자 ID {}의 카메라 목록 조회", userId);
        List<Camera> cameras = cameraRepository.findByUserId(userId);
        log.info("카메라 {}개 조회됨", cameras.size());
        return cameras;
    }
    
    public Camera getCameraById(String cameraId, UUID userId) {
        log.info("카메라 ID {} 조회 (사용자 ID: {})", cameraId, userId);
        
        Camera camera = cameraRepository.findById(cameraId)
                .orElseThrow(() -> new IllegalArgumentException("카메라를 찾을 수 없습니다: " + cameraId));
        
        // 사용자 소유권 확인
        if (!camera.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("해당 카메라에 대한 접근 권한이 없습니다");
        }
        
        return camera;
    }
    
    @Transactional
    public Camera updateCameraStatus(String cameraId, Camera.CameraStatus newStatus, UUID userId) {
        log.info("카메라 {} 상태 변경: {} (사용자 ID: {})", cameraId, newStatus, userId);
        
        // 카메라 조회 및 권한 확인
        Camera camera = getCameraById(cameraId, userId);
        
        // 상태 변경
        Camera.CameraStatus oldStatus = camera.getStatus();
        camera.setStatus(newStatus);
        
        Camera updatedCamera = cameraRepository.save(camera);
        log.info("카메라 {} 상태 변경 완료: {} -> {}", cameraId, oldStatus, newStatus);
        
        return updatedCamera;
    }
    
    @Transactional
    public Camera createCamera(CameraController.CameraCreateRequest request, UUID userId) {
        log.info("카메라 생성: {} (사용자 ID: {})", request.getName(), userId);
        
        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));
        
        // 카메라 ID 생성 (cam-001, cam-002, ... 형태)
        String newCameraId = generateCameraId();
        
        // 카메라 엔티티 생성
        Camera camera = new Camera();
        camera.setId(newCameraId);
        camera.setUser(user);
        camera.setName(request.getName());
        camera.setLat(request.getLat());
        camera.setLng(request.getLng());
        camera.setRtspUrl(request.getRtspUrl());
        camera.setStreamUrl("http://detector:5001/stream/" + newCameraId); // 스트림 URL 자동 생성
        camera.setStatus(Camera.CameraStatus.OFFLINE); // 기본 상태: OFFLINE
        camera.setYoloEnabled(request.isYoloEnabled()); // YOLO 활성화 여부
        camera.setMetaJson(request.getDescription());
        
        Camera savedCamera = cameraRepository.save(camera);
        log.info("카메라 생성 완료: {}", savedCamera.getId());
        
        return savedCamera;
    }
    
    @Transactional
    public Camera updateCamera(String cameraId, CameraController.CameraUpdateRequest request, UUID userId) {
        log.info("카메라 {} 수정 (사용자 ID: {})", cameraId, userId);
        
        // 카메라 조회 및 권한 확인
        Camera camera = getCameraById(cameraId, userId);
        
        // 필드 업데이트
        camera.setName(request.getName());
        camera.setLat(request.getLat());
        camera.setLng(request.getLng());
        camera.setRtspUrl(request.getRtspUrl());
        camera.setYoloEnabled(request.isYoloEnabled());
        camera.setMetaJson(request.getDescription());
        
        Camera updatedCamera = cameraRepository.save(camera);
        log.info("카메라 {} 수정 완료", cameraId);
        
        return updatedCamera;
    }
    
    @Transactional
    public void deleteCamera(String cameraId, UUID userId) {
        log.info("카메라 {} 삭제 (사용자 ID: {})", cameraId, userId);
        
        // cam-001, cam-002는 삭제 불가
        if ("cam-001".equals(cameraId) || "cam-002".equals(cameraId)) {
            throw new IllegalArgumentException("기본 카메라는 삭제할 수 없습니다: " + cameraId);
        }
        
        // 카메라 조회 및 권한 확인
        Camera camera = getCameraById(cameraId, userId);
        
        // 카메라 삭제
        cameraRepository.delete(camera);
        log.info("카메라 {} 삭제 완료", cameraId);
    }
    
    private String generateCameraId() {
        // 기존 카메라 ID 중 가장 큰 번호를 찾아서 +1
        List<Camera> allCameras = cameraRepository.findAll();
        int maxNumber = 0;
        
        for (Camera camera : allCameras) {
            String id = camera.getId();
            if (id.startsWith("cam-")) {
                try {
                    int number = Integer.parseInt(id.substring(4));
                    maxNumber = Math.max(maxNumber, number);
                } catch (NumberFormatException e) {
                    // 숫자가 아닌 경우 무시
                }
            }
        }
        
        return String.format("cam-%03d", maxNumber + 1);
    }
}
