# Build stage
FROM bellsoft/liberica-openjdk-alpine:17 AS builder

WORKDIR /app

COPY gradlew build.gradle settings.gradle /app/
COPY gradle /app/gradle
RUN chmod +x gradlew
RUN ./gradlew dependencies --no-daemon

COPY . .
RUN ./gradlew clean build -x test --no-daemon

# Run stage
FROM bellsoft/liberica-openjdk-alpine:17

WORKDIR /app

# JAR 파일 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 필요한 리소스 파일 복사
COPY src/main/resources/application.yml /app/config/application.yml

EXPOSE 8080

# Spring Boot가 설정 파일을 읽도록 환경 변수 설정
ENTRYPOINT ["java", "-jar", "app.jar"]
CMD ["--spring.config.location=file:/app/config/application.yml"]