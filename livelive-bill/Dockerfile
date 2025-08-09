# 1. 빌드를 위한 Gradle 빌드 이미지
FROM gradle:7.6-jdk17 AS build

# 프로젝트 작업 디렉토리 설정
WORKDIR /app

# Gradle Wrapper와 프로젝트 파일 복사
COPY . /app

# Gradle Wrapper를 사용하여 빌드 실행
RUN chmod +x ./gradlew
RUN ./gradlew build --no-daemon

# 2. 실행을 위한 OpenJDK 17 이미지
FROM openjdk:17-jdk-slim

# 실행할 작업 디렉토리 설정
WORKDIR /app

# 빌드된 JAR 파일 복사 (위 빌드 단계에서 생성된 JAR)
COPY --from=build /app/build/libs/*.jar app.jar

# Spring Boot 애플리케이션 실행
ENTRYPOINT ["nohup", "java", "-jar", "app.jar", "&"]