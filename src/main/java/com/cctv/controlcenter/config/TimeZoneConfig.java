package com.cctv.controlcenter.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.util.TimeZone;

@Configuration
public class TimeZoneConfig {

    @Autowired
    private ObjectMapper objectMapper;

    @PostConstruct
    public void init() {
        // JVM 전체 기본 시간대를 한국 시간으로 설정
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
        
        // Jackson ObjectMapper의 시간대를 한국 시간으로 설정
        objectMapper.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        
        System.out.println("✅ 시간대가 한국 시간(Asia/Seoul)으로 설정되었습니다.");
    }
}
