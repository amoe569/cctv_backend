package com.cctv.controlcenter.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public class TrafficEventRequest {
    
    @NotBlank(message = "카메라 ID는 필수입니다")
    @Size(max = 50, message = "카메라 ID는 50자를 초과할 수 없습니다")
    private String cameraId;
    
    @NotNull(message = "이벤트 타입은 필수입니다")
    @Size(max = 100, message = "이벤트 타입은 100자를 초과할 수 없습니다")
    private String type;
    
    @NotNull(message = "심각도는 필수입니다")
    @Min(value = 1, message = "심각도는 1 이상이어야 합니다")
    @Max(value = 5, message = "심각도는 5 이하여야 합니다")
    private Integer severity;
    
    @NotNull(message = "점수는 필수입니다")
    @DecimalMin(value = "0.0", message = "점수는 0.0 이상이어야 합니다")
    @DecimalMax(value = "1.0", message = "점수는 1.0 이하여야 합니다")
    private Double score;
    
    @NotNull(message = "타임스탬프는 필수입니다")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss[.SSS]")
    private String ts;  // LocalDateTime에서 String으로 변경
    
    private BoundingBox boundingBox;
    
    @NotNull(message = "차량 수는 필수입니다")
    @Min(value = 1, message = "차량 수는 1 이상이어야 합니다")
    private Integer vehicleCount;
    
    @NotBlank(message = "메시지는 필수입니다")
    @Size(max = 500, message = "메시지는 500자를 초과할 수 없습니다")
    private String message;
    
    // Constructors
    public TrafficEventRequest() {}
    
    // Getters and Setters
    public String getCameraId() { return cameraId; }
    public void setCameraId(String cameraId) { this.cameraId = cameraId; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public Integer getSeverity() { return severity; }
    public void setSeverity(Integer severity) { this.severity = severity; }
    
    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }
    
    public String getTs() { return ts; }
    public void setTs(String ts) { this.ts = ts; }
    
    public BoundingBox getBoundingBox() { return boundingBox; }
    public void setBoundingBox(BoundingBox boundingBox) { this.boundingBox = boundingBox; }
    
    public Integer getVehicleCount() { return vehicleCount; }
    public void setVehicleCount(Integer vehicleCount) { this.vehicleCount = vehicleCount; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public static class BoundingBox {
        private Integer x;
        private Integer y;
        private Integer w;
        private Integer h;
        
        // Constructors
        public BoundingBox() {}
        
        // Getters and Setters
        public Integer getX() { return x; }
        public void setX(Integer x) { this.x = x; }
        
        public Integer getY() { return y; }
        public void setY(Integer y) { this.y = y; }
        
        public Integer getW() { return w; }
        public void setW(Integer w) { this.w = w; }
        
        public Integer getH() { return h; }
        public void setH(Integer h) { this.h = h; }
    }
}
