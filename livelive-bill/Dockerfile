# 1. Gradle 빌드를 위한 이미지
FROM gradle:8.5-jdk17 AS builder

# 2. 프로젝트 복사 및 빌드
WORKDIR /app
COPY --chown=gradle:gradle . .

# 3. 실제 실행 환경 이미지
FROM eclipse-temurin:17-jdk
WORKDIR /app

# 4. 빌드 결과 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 5. 실행 명령
ENTRYPOINT ["java", "-jar", "app.jar"]
