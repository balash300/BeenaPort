# Build stage
FROM gradle:8.5-jdk21 AS builder

WORKDIR /app

COPY . .

# Give permission
RUN chmod +x gradlew

# Use wrapper (IMPORTANT)
RUN ./gradlew build -x test

# Run stage
FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]