# 1단계: 빌드 단계
FROM gradle:8.5-jdk17 AS builder
WORKDIR /app

# Gradle 캐시 최적화 (설정파일 먼저 복사)
COPY build.gradle settings.gradle /app/

# Gradle 캐시 미리 생성
RUN gradle dependencies --no-daemon || true

# 나머지 소스 복사
COPY . /app/

# 테스트 스킵하고 빌드
RUN gradle clean build -x test --no-daemon

# 2단계: 실행 단계
FROM eclipse-temurin:17-jdk
WORKDIR /app

# 빌드된 JAR 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
