package com.cctv.controlcenter.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CCTV AI 관제 시스템 API")
                        .description("Spring Boot + Python/YOLOv5 기반 CCTV AI 관제 시스템의 REST API 문서")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("CCTV AI Team")
                                .email("admin@cctv-ai.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("개발 서버"),
                        new Server().url("https://api.cctv-ai.com").description("프로덕션 서버")
                ));
    }
}
