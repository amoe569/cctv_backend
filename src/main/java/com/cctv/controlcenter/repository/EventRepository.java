package com.cctv.controlcenter.repository;

import com.cctv.controlcenter.domain.Event;
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
public interface EventRepository extends JpaRepository<Event, UUID> {
    
    List<Event> findByCameraIdOrderByTsDesc(String cameraId);
    
    @Query("SELECT e FROM Event e WHERE e.camera.id = :cameraId ORDER BY e.ts DESC")
    Page<Event> findByCameraIdOrdered(@Param("cameraId") String cameraId, Pageable pageable);
    
    @Query("SELECT e FROM Event e WHERE e.camera.id = :cameraId AND e.ts >= :from ORDER BY e.ts DESC")
    List<Event> findByCameraIdAndTimeFrom(@Param("cameraId") String cameraId, @Param("from") LocalDateTime from);
    
    @Query("SELECT e FROM Event e WHERE e.camera.id = :cameraId AND e.score >= :minScore ORDER BY e.ts DESC")
    List<Event> findByCameraIdAndMinScore(@Param("cameraId") String cameraId, @Param("minScore") Double minScore);
    
    @Query("SELECT e FROM Event e WHERE e.camera.id IN :cameraIds ORDER BY e.ts DESC")
    Page<Event> findByCameraIdsOrdered(@Param("cameraIds") List<String> cameraIds, Pageable pageable);
    
    @Query("SELECT e FROM Event e WHERE " +
           "(:cameraId IS NULL OR e.camera.id = :cameraId) AND " +
           "(:eventType IS NULL OR e.type LIKE CONCAT('%', :eventType, '%')) AND " +
           "(:startDate IS NULL OR e.ts >= :startDate) AND " +
           "(:endDate IS NULL OR e.ts <= :endDate) AND " +
           "e.severity >= :minSeverity " +
           "ORDER BY e.ts DESC")
    Page<Event> findEventsWithFilters(
            @Param("cameraId") String cameraId,
            @Param("eventType") String eventType,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("minSeverity") int minSeverity,
            Pageable pageable);
}
