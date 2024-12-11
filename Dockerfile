# Build stage
FROM gradle:8.5-jdk17 AS builder
WORKDIR /app

COPY gradle /app/gradle
COPY gradlew /app/gradlew
COPY build.gradle /app/build.gradle
COPY settings.gradle /app/settings.gradle
COPY src /app/src

RUN ./gradlew bootJar --no-daemon -x test

# Run stage
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar /app/weather.jar

EXPOSE 8080

CMD ["java", "-jar", "weather.jar"]
