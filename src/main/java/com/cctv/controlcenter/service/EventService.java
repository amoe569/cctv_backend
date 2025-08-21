package com.cctv.controlcenter.service;

import com.cctv.controlcenter.api.dto.EventCreateRequest;
import com.cctv.controlcenter.api.dto.TrafficEventRequest;
import com.cctv.controlcenter.domain.Camera;
import com.cctv.controlcenter.domain.Event;
import com.cctv.controlcenter.domain.Video;
import com.cctv.controlcenter.repository.CameraRepository;
import com.cctv.controlcenter.repository.EventRepository;
import com.cctv.controlcenter.repository.VideoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class EventService {
    
    private static final Logger log = LoggerFactory.getLogger(EventService.class);
    
    private final EventRepository eventRepository;
    private final CameraRepository cameraRepository;
    private final VideoRepository videoRepository;
    
    private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    
    public EventService(EventRepository eventRepository, CameraRepository cameraRepository, VideoRepository videoRepository) {
        this.eventRepository = eventRepository;
        this.cameraRepository = cameraRepository;
        this.videoRepository = videoRepository;
    }
    
    @Transactional
    public Event createEvent(EventCreateRequest request) {
        log.info("이벤트 생성 요청: {}", request);
        
        // 카메라 존재 여부 확인
        Camera camera = cameraRepository.findById(request.getCameraId())
                .orElseThrow(() -> new IllegalArgumentException("카메라를 찾을 수 없습니다: " + request.getCameraId()));
        
        // 비디오 ID가 제공된 경우 존재 여부 확인
        Video video = null;
        if (request.getVideoId() != null) {
            try {
                UUID videoId = UUID.fromString(request.getVideoId());
                video = videoRepository.findById(videoId)
                        .orElseThrow(() -> new IllegalArgumentException("비디오를 찾을 수 없습니다: " + request.getVideoId()));
            } catch (IllegalArgumentException e) {
                log.warn("잘못된 비디오 ID 형식: {}", request.getVideoId());
            }
        }
        
        // 이벤트 생성
        Event event = new Event();
        event.setId(UUID.randomUUID());
        event.setCamera(camera);
        event.setVideo(video);
        event.setTs(request.getTs());
        event.setType(request.getType());
        event.setSeverity(request.getSeverity());
        event.setScore(request.getScore());
        
        // 바운딩 박스를 JSON으로 저장
        if (request.getBoundingBox() != null) {
            String bboxJson = String.format(
                "{\"x\":%d,\"y\":%d,\"w\":%d,\"h\":%d}",
                request.getBoundingBox().getX(),
                request.getBoundingBox().getY(),
                request.getBoundingBox().getW(),
                request.getBoundingBox().getH()
            );
            event.setBboxJson(bboxJson);
        }
        
        Event savedEvent = eventRepository.save(event);
        log.info("이벤트 생성 완료: id={}, type={}, score={}", savedEvent.getId(), savedEvent.getType(), savedEvent.getScore());
        
        // SSE 구독자들에게 이벤트 브로드캐스트
        broadcastEvent(savedEvent);
        
        return savedEvent;
    }
    
    @Transactional
    public Event createTrafficEvent(TrafficEventRequest request) {
        log.info("통행량 많음 이벤트 생성 요청: {}", request);
        
        // 카메라 존재 여부 확인
        Camera camera = cameraRepository.findById(request.getCameraId())
                .orElseThrow(() -> new IllegalArgumentException("카메라를 찾을 수 없습니다: " + request.getCameraId()));
        
        // 카메라 상태를 WARNING으로 변경
        camera.setStatus(Camera.CameraStatus.WARNING);
        cameraRepository.save(camera);
        log.info("카메라 {} 상태를 WARNING으로 변경", request.getCameraId());
        
        // String 타임스탬프를 LocalDateTime으로 변환
        LocalDateTime eventTime;
        try {
            eventTime = LocalDateTime.parse(request.getTs(), DateTimeFormatter.ISO_DATE_TIME);
        } catch (Exception e) {
            log.warn("타임스탬프 파싱 실패, 현재 시간 사용: {}", request.getTs());
            eventTime = LocalDateTime.now();
        }
        
        // 이벤트 생성
        Event event = new Event();
        event.setId(UUID.randomUUID());
        event.setCamera(camera);
        event.setTs(eventTime);
        event.setType(request.getType());
        event.setSeverity(request.getSeverity());
        event.setScore(request.getScore());
        
        // 바운딩 박스를 JSON으로 저장
        if (request.getBoundingBox() != null) {
            String bboxJson = String.format(
                "{\"x\":%d,\"y\":%d,\"w\":%d,\"h\":%d}",
                request.getBoundingBox().getX(),
                request.getBoundingBox().getY(),
                request.getBoundingBox().getW(),
                request.getBoundingBox().getH()
            );
            event.setBboxJson(bboxJson);
        }
        
        // 차량 수와 메시지를 메타 JSON에 저장
        String metaJson = String.format(
            "{\"vehicleCount\":%d,\"message\":\"%s\"}",
            request.getVehicleCount(),
            request.getMessage()
        );
        event.setMetaJson(metaJson);
        
        Event savedEvent = eventRepository.save(event);
        log.info("통행량 많음 이벤트 생성 완료: id={}, 차량수={}, 메시지={}", 
                savedEvent.getId(), request.getVehicleCount(), request.getMessage());
        
        // SSE 구독자들에게 이벤트 브로드캐스트
        broadcastEvent(savedEvent);
        
        return savedEvent;
    }
    
    public List<Event> getEventsByCamera(String cameraId) {
        return eventRepository.findByCameraIdOrderByTsDesc(cameraId);
    }
    
    public Page<Event> getEventsWithFilters(String cameraId, String eventType, 
            LocalDateTime startDate, LocalDateTime endDate, int minSeverity, Pageable pageable) {
        
        log.info("이벤트 필터링 조회 - 카메라: {}, 타입: {}, 시작: {}, 종료: {}, 최소심각도: {}", 
                cameraId, eventType, startDate, endDate, minSeverity);
        
        try {
            // 필터 조건에 따라 적절한 메서드 선택
            if (cameraId != null && !cameraId.isEmpty() && eventType != null && !eventType.isEmpty()) {
                // 카메라 ID + 이벤트 타입 + 심각도
                return eventRepository.findByCameraIdAndTypeAndSeverityGreaterThanEqualOrderByTsDesc(
                        cameraId, eventType, minSeverity, pageable);
            } else if (cameraId != null && !cameraId.isEmpty()) {
                // 카메라 ID + 심각도만
                return eventRepository.findByCameraIdAndSeverityGreaterThanEqualOrderByTsDesc(
                        cameraId, minSeverity, pageable);
            } else if (eventType != null && !eventType.isEmpty()) {
                // 이벤트 타입 + 심각도만
                return eventRepository.findByTypeAndSeverityGreaterThanEqualOrderByTsDesc(
                        eventType, minSeverity, pageable);
            } else {
                // 심각도만
                return eventRepository.findBySeverityGreaterThanEqualOrderByTsDesc(
                        minSeverity, pageable);
            }
        } catch (Exception e) {
            log.error("이벤트 필터링 조회 실패", e);
            // 오류 발생 시 빈 페이지 반환
            return Page.empty(pageable);
        }
    }
    
    public SseEmitter subscribeToEvents() {
        SseEmitter emitter = new SseEmitter(0L); // 무한 타임아웃
        emitters.add(emitter);
        
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError((ex) -> emitters.remove(emitter));
        
        // 즉시 연결 확인 메시지 전송
        try {
            emitter.send(SseEmitter.event()
                    .name("connected")
                    .data("SSE 연결 성공"));
        } catch (IOException e) {
            log.warn("SSE 초기 연결 메시지 전송 실패", e);
            emitters.remove(emitter);
        }
        
        log.info("SSE 구독 추가: 현재 구독자 수 = {}", emitters.size());
        
        return emitter;
    }
    
    private void broadcastEvent(Event event) {
        emitters.removeIf(emitter -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("event")
                        .data(event));
                return false;
            } catch (IOException e) {
                log.warn("SSE 이벤트 전송 실패", e);
                return true;
            }
        });
        
        log.info("이벤트 브로드캐스트 완료: 구독자 수 = {}", emitters.size());
    }
    
    // 10초마다 하트비트 전송으로 연결 유지
    @Scheduled(fixedRate = 10000)
    public void sendHeartbeat() {
        if (!emitters.isEmpty()) {
            emitters.removeIf(emitter -> {
                try {
                    emitter.send(SseEmitter.event()
                            .name("heartbeat")
                            .data(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));
                    return false;
                } catch (IOException e) {
                    log.debug("하트비트 전송 실패 - 연결 제거");
                    return true;
                }
            });
        }
    }
}
