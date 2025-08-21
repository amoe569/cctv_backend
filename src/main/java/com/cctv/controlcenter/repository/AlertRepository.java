package com.cctv.controlcenter.repository;

import com.cctv.controlcenter.domain.Alert;
import com.cctv.controlcenter.domain.Alert.AlertState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AlertRepository extends JpaRepository<Alert, UUID> {
    
    List<Alert> findByCameraId(String cameraId);
    
    List<Alert> findByCameraIdAndState(String cameraId, AlertState state);
    
    @Query("SELECT a FROM Alert a WHERE a.camera.id = :cameraId ORDER BY a.createdAt DESC")
    Page<Alert> findByCameraIdOrdered(@Param("cameraId") String cameraId, Pageable pageable);
    
    @Query("SELECT a FROM Alert a WHERE a.camera.id IN :cameraIds AND a.state = :state ORDER BY a.createdAt DESC")
    Page<Alert> findByCameraIdsAndStateOrdered(
        @Param("cameraIds") List<String> cameraIds,
        @Param("state") AlertState state,
        Pageable pageable
    );
    
    long countByCameraIdAndState(String cameraId, AlertState state);
}
