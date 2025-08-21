package com.cctv.controlcenter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import javax.sql.DataSource;

@SpringBootApplication
@EnableAsync
public class CctvControlCenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(CctvControlCenterApplication.class, args);
    }

    @Bean
    public CommandLineRunner dataLoader(DataSource dataSource) {
        return args -> {
            System.out.println("데이터베이스 초기화 시작...");
            try {
                ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
                populator.addScript(new ClassPathResource("db/data.sql"));
                populator.setContinueOnError(true); // 오류 발생 시 계속 진행
                populator.execute(dataSource);
                System.out.println("데이터베이스 초기화 완료!");
            } catch (Exception e) {
                System.out.println("데이터베이스 초기화 중 오류 발생 (무시됨): " + e.getMessage());
            }
        };
    }
}
