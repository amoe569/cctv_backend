package com.cctv.controlcenter.repository;

import com.cctv.controlcenter.domain.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface VideoRepository extends JpaRepository<Video, UUID> {
    
    List<Video> findByCameraId(String cameraId);
    
    @Query("SELECT v FROM Video v WHERE v.camera.id = :cameraId AND v.startTs >= :from AND v.endTs <= :to ORDER BY v.startTs DESC")
    Page<Video> findByCameraIdAndTimeRange(
        @Param("cameraId") String cameraId,
        @Param("from") LocalDateTime from,
        @Param("to") LocalDateTime to,
        Pageable pageable
    );
    
    @Query("SELECT v FROM Video v WHERE v.camera.id = :cameraId ORDER BY v.startTs DESC")
    Page<Video> findByCameraIdOrdered(@Param("cameraId") String cameraId, Pageable pageable);
    
    boolean existsByCameraIdAndPath(String cameraId, String path);
}
