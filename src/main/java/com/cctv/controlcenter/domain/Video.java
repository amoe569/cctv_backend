package com.cctv.controlcenter.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "videos")
public class Video {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "camera_id", nullable = false)
    private Camera camera;
    
    @Column(name = "start_ts", nullable = false)
    private LocalDateTime startTs;
    
    @Column(name = "end_ts", nullable = false)
    private LocalDateTime endTs;
    
    @Column(nullable = false)
    private String path;
    
    @Column(name = "file_size_bytes")
    private Long fileSizeBytes;
    
    @Column(name = "duration_sec")
    private Integer durationSec;
    
    private String checksum;
    
    private String codec;
    
    @Column(name = "thumb_path")
    private String thumbPath;
    
    @Column(name = "meta_json", columnDefinition = "TEXT")
    private String metaJson;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    // Constructors
    public Video() {}
    
    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public Camera getCamera() { return camera; }
    public void setCamera(Camera camera) { this.camera = camera; }
    
    public LocalDateTime getStartTs() { return startTs; }
    public void setStartTs(LocalDateTime startTs) { this.startTs = startTs; }
    
    public LocalDateTime getEndTs() { return endTs; }
    public void setEndTs(LocalDateTime endTs) { this.endTs = endTs; }
    
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    
    public Long getFileSizeBytes() { return fileSizeBytes; }
    public void setFileSizeBytes(Long fileSizeBytes) { this.fileSizeBytes = fileSizeBytes; }
    
    public Integer getDurationSec() { return durationSec; }
    public void setDurationSec(Integer durationSec) { this.durationSec = durationSec; }
    
    public String getChecksum() { return checksum; }
    public void setChecksum(String checksum) { this.checksum = checksum; }
    
    public String getCodec() { return codec; }
    public void setCodec(String codec) { this.codec = codec; }
    
    public String getThumbPath() { return thumbPath; }
    public void setThumbPath(String thumbPath) { this.thumbPath = thumbPath; }
    
    public String getMetaJson() { return metaJson; }
    public void setMetaJson(String metaJson) { this.metaJson = metaJson; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
