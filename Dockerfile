# Build stage
FROM maven:3.9.9-eclipse-temurin-17-alpine AS build
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
COPY custom_check.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests -B

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Create non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --from=build /app/target/*.jar app.jar

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT [
  "java",
  "-XX:+UseContainerSupport",
  "-XX:+UseG1GC",
  "-XX:MaxGCPauseMillis=300",
  "-XX:MaxRAMPercentage=70.0",
  "-XX:InitialRAMPercentage=50.0",
  "-XX:+ExitOnOutOfMemoryError",
  "-jar",
  "app.jar"
]