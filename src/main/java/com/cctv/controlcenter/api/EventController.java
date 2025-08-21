package com.cctv.controlcenter.api;

import com.cctv.controlcenter.api.dto.EventCreateRequest;
import com.cctv.controlcenter.api.dto.TrafficEventRequest;
import com.cctv.controlcenter.domain.Event;
import com.cctv.controlcenter.dto.EventDTO;
import com.cctv.controlcenter.service.EventService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {
    
    private static final Logger log = LoggerFactory.getLogger(EventController.class);
    
    private final EventService eventService;
    
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }
    
    @PostMapping
    public ResponseEntity<Event> createEvent(@Valid @RequestBody EventCreateRequest request) {
        log.info("이벤트 생성 요청: {}", request);
        Event event = eventService.createEvent(request);
        return ResponseEntity.ok(event);
    }
    
    @GetMapping
    public ResponseEntity<Page<EventDTO>> getEvents(
            @RequestParam(required = false) String cameraId,
            @RequestParam(required = false) String eventType,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "0") Integer severity,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        
        log.info("이벤트 목록 조회: cameraId={}, eventType={}, startDate={}, endDate={}, severity={}, page={}, size={}", 
                cameraId, eventType, startDate, endDate, severity, page, size);
        
        try {
            LocalDateTime startDateTime = null;
            LocalDateTime endDateTime = null;
            
            if (startDate != null && !startDate.isEmpty()) {
                try {
                    startDateTime = LocalDate.parse(startDate).atStartOfDay();
                } catch (Exception e) {
                    log.warn("잘못된 시작일 형식: {}", startDate);
                }
            }
            if (endDate != null && !endDate.isEmpty()) {
                try {
                    endDateTime = LocalDate.parse(endDate).atTime(23, 59, 59);
                } catch (Exception e) {
                    log.warn("잘못된 종료일 형식: {}", endDate);
                }
            }
            
            Pageable pageable = PageRequest.of(page, size);
            Page<Event> events = eventService.getEventsWithFilters(cameraId, eventType, startDateTime, endDateTime, severity, pageable);
            
            // Event를 EventDTO로 변환
            Page<EventDTO> eventDTOs = events.map(EventDTO::new);
            
            log.info("이벤트 목록 조회 완료: {}개", eventDTOs.getTotalElements());
            return ResponseEntity.ok(eventDTOs);
            
        } catch (Exception e) {
            log.error("이벤트 목록 조회 실패", e);
            // 에러가 발생해도 빈 페이지 반환하여 500 오류 방지
            Pageable pageable = PageRequest.of(page, size);
            return ResponseEntity.ok(Page.empty(pageable));
        }
    }

    @GetMapping("/stream")
    public SseEmitter streamEvents() {
        log.info("SSE 이벤트 스트림 구독 요청");
        return eventService.subscribeToEvents();
    }
    
    @GetMapping("/camera/{cameraId}")
    public ResponseEntity<List<Event>> getEventsByCamera(@PathVariable String cameraId) {
        log.info("카메라 {}의 이벤트 목록 조회", cameraId);
        List<Event> events = eventService.getEventsByCamera(cameraId);
        return ResponseEntity.ok(events);
    }
    
    @PostMapping("/traffic")
    public ResponseEntity<Event> createTrafficEvent(@Valid @RequestBody TrafficEventRequest request) {
        log.info("통행량 많음 이벤트 생성 요청: {}", request);
        Event event = eventService.createTrafficEvent(request);
        return ResponseEntity.ok(event);
    }
}
