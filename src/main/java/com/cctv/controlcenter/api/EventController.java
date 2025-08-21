package com.cctv.controlcenter.api;

import com.cctv.controlcenter.api.dto.EventCreateRequest;
import com.cctv.controlcenter.api.dto.TrafficEventRequest;
import com.cctv.controlcenter.domain.Event;
import com.cctv.controlcenter.service.EventService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

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
