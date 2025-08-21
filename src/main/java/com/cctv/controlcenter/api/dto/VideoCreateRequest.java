package com.cctv.controlcenter.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public class VideoCreateRequest {
    
    @NotBlank(message = "카메라 ID는 필수입니다")
    @Size(max = 50, message = "카메라 ID는 50자를 초과할 수 없습니다")
    private String cameraId;
    
    @NotNull(message = "시작 시간은 필수입니다")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startTs;
    
    @NotNull(message = "종료 시간은 필수입니다")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endTs;
    
    @NotBlank(message = "파일 경로는 필수입니다")
    private String path;
    
    @Min(value = 1, message = "파일 크기는 1바이트 이상이어야 합니다")
    private Long fileSizeBytes;
    
    private String checksum;
    
    private String codec;
    
    // Constructors
    public VideoCreateRequest() {}
    
    // Getters and Setters
    public String getCameraId() { return cameraId; }
    public void setCameraId(String cameraId) { this.cameraId = cameraId; }
    
    public LocalDateTime getStartTs() { return startTs; }
    public void setStartTs(LocalDateTime startTs) { this.startTs = startTs; }
    
    public LocalDateTime getEndTs() { return endTs; }
    public void setEndTs(LocalDateTime endTs) { this.endTs = endTs; }
    
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    
    public Long getFileSizeBytes() { return fileSizeBytes; }
    public void setFileSizeBytes(Long fileSizeBytes) { this.fileSizeBytes = fileSizeBytes; }
    
    public String getChecksum() { return checksum; }
    public void setChecksum(String checksum) { this.checksum = checksum; }
    
    public String getCodec() { return codec; }
    public void setCodec(String codec) { this.codec = codec; }
}
