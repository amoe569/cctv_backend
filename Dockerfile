FROM openjdk:17-jdk-slim

WORKDIR /app

# Gradle wrapper 및 build.gradle 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# 의존성 다운로드
RUN chmod +x gradlew
RUN ./gradlew dependencies --no-daemon

# 소스 코드 복사
COPY src src

# 애플리케이션 빌드
RUN ./gradlew build -x test --no-daemon

# JAR 파일 실행
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "build/libs/cctv-control-center-0.0.1-SNAPSHOT.jar"]
