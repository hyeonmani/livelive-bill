# 1단계: Gradle 빌드
FROM gradle:8.5-jdk17 AS builder

WORKDIR /app

# Gradle 설정 파일만 복사 (디렉토리로 끝남)
COPY build.gradle settings.gradle gradlew gradlew.bat /app/
COPY gradle /app/gradle/

RUN ./gradlew dependencies --no-daemon || return 0

# 나머지 소스 복사
COPY . /app/

RUN ./gradlew clean build -x test --no-daemon


# 2단계: 실행 환경
FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
