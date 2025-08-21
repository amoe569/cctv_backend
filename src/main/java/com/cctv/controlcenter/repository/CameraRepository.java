package com.cctv.controlcenter.repository;

import com.cctv.controlcenter.domain.Camera;
import com.cctv.controlcenter.domain.Camera.CameraStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CameraRepository extends JpaRepository<Camera, String> {
    
    List<Camera> findByUserId(UUID userId);
    
    List<Camera> findByUserIdAndStatus(UUID userId, CameraStatus status);
    
    @Query("SELECT c FROM Camera c WHERE c.user.id = :userId ORDER BY c.createdAt DESC")
    List<Camera> findUserCamerasOrdered(@Param("userId") UUID userId);
    
    boolean existsByIdAndUserId(String id, UUID userId);
}
