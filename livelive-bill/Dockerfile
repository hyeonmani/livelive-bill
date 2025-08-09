# 1단계: 빌드 단계
FROM bellsoft/liberica-openjdk-alpine:17 AS builder
WORKDIR /app

COPY . .

# Gradle 캐시 미리 생성
RUN ./gradlew clean build -x test

# 2단계: 실행 단계
FROM bellsoft/liberica-openjdk-alpine:17

WORKDIR /app

# 빌드된 JAR 복사
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]