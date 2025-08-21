package com.cctv.controlcenter.dto;

import com.cctv.controlcenter.domain.Event;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.UUID;

public class EventDTO {
    
    private UUID id;
    private String cameraId;
    private String cameraName;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime ts;
    
    private String type;
    private Integer severity;
    private Double score;
    private String bboxJson;
    private String metaJson;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    
    // 기본 생성자
    public EventDTO() {}
    
    // Event 엔티티로부터 DTO 생성
    public EventDTO(Event event) {
        this.id = event.getId();
        this.cameraId = event.getCamera() != null ? event.getCamera().getId() : null;
        this.cameraName = event.getCamera() != null ? event.getCamera().getName() : null;
        this.ts = event.getTs();
        this.type = event.getType();
        this.severity = event.getSeverity();
        this.score = event.getScore();
        this.bboxJson = event.getBboxJson();
        this.metaJson = event.getMetaJson();
        this.createdAt = event.getCreatedAt();
    }
    
    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public String getCameraId() { return cameraId; }
    public void setCameraId(String cameraId) { this.cameraId = cameraId; }
    
    public String getCameraName() { return cameraName; }
    public void setCameraName(String cameraName) { this.cameraName = cameraName; }
    
    public LocalDateTime getTs() { return ts; }
    public void setTs(LocalDateTime ts) { this.ts = ts; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public Integer getSeverity() { return severity; }
    public void setSeverity(Integer severity) { this.severity = severity; }
    
    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }
    
    public String getBboxJson() { return bboxJson; }
    public void setBboxJson(String bboxJson) { this.bboxJson = bboxJson; }
    
    public String getMetaJson() { return metaJson; }
    public void setMetaJson(String metaJson) { this.metaJson = metaJson; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
