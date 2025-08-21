# CCTV Control Center Backend

Spring Boot 기반의 CCTV 관제 시스템 백엔드 API 서버입니다.

## 🚀 기술 스택

- **Spring Boot 3.2.0** with Java 17
- **Spring Data JPA** for database operations
- **Spring Security** for authentication
- **H2 Database** (개발용) / **PostgreSQL** (프로덕션용)
- **Server-Sent Events (SSE)** for real-time communication
- **Gradle** for build automation

## 📦 설치 및 실행

### 필수 요구사항
- Java 17+
- Gradle 8.0+

### 설치 및 실행
```bash
# 프로젝트 디렉토리로 이동
cd control-center

# Java 환경변수 설정 (macOS)
export JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home
export PATH="$JAVA_HOME/bin:$PATH"

# Gradle 래퍼로 실행
./gradlew bootRun
```

### 빌드
```bash
./gradlew build
```

## 🌐 접속

- **API 서버**: http://localhost:8080
- **API 문서 (Swagger)**: http://localhost:8080/swagger-ui/index.html
- **H2 콘솔**: http://localhost:8080/h2-console

## 📁 프로젝트 구조

```
src/main/java/com/cctv/controlcenter/
├── api/                 # REST API 컨트롤러
│   ├── CameraController.java
│   └── EventController.java
├── domain/              # JPA 엔티티
│   ├── Camera.java
│   └── Event.java
├── repository/          # 데이터 접근 계층
│   ├── CameraRepository.java
│   └── EventRepository.java
├── service/             # 비즈니스 로직
│   ├── CameraService.java
│   └── EventService.java
└── config/              # 설정 클래스
    └── CorsConfig.java
```

## 🔧 환경 설정

`application.properties` 또는 `application.yml`에서 다음 설정을 확인하세요:

```properties
# 데이터베이스 설정
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# JPA 설정
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop

# H2 콘솔 활성화
spring.h2.console.enabled=true
```

## 📡 API 엔드포인트

### 카메라 관리
- `GET /api/cameras` - 모든 카메라 조회
- `GET /api/cameras/{id}` - 특정 카메라 조회
- `PUT /api/cameras/{id}/status` - 카메라 상태 변경

### 이벤트 관리
- `GET /api/events` - 모든 이벤트 조회
- `GET /api/events/camera/{cameraId}` - 카메라별 이벤트 조회
- `POST /api/events/traffic` - 교통 이벤트 생성

### 실시간 통신
- `GET /api/events/stream` - SSE 이벤트 스트림

## 🗄️ 데이터베이스

- **개발 환경**: H2 인메모리 데이터베이스
- **프로덕션 환경**: PostgreSQL
- **JPA/Hibernate** ORM 사용
- **자동 스키마 생성** (개발 환경)

## 🔒 보안

- **CORS** 설정으로 프론트엔드와 통신
- **Spring Security** 기본 설정
- **JWT 토큰** 기반 인증 (향후 구현 예정)
