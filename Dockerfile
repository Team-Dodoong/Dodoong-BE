# 빌드 스테이지
FROM eclipse-temurin:17-jdk AS builder
WORKDIR /app

# Gradle 파일들 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./

# 의존성 다운로드
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon || true

# 소스 코드 복사 후 빌드
COPY src src
RUN ./gradlew clean bootjar --no-daemon -x test

# 실행 스테이지
FROM eclipse-temurin:17-jre
WORKDIR /app

RUN groupadd -r spring && useradd -r -g spring spring
USER spring:spring

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]