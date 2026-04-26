FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Download OpenTelemetry Java agent (cached by Docker layer)
ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v2.22.0/opentelemetry-javaagent.jar /app/libs/opentelemetry-javaagent.jar

COPY target/*.jar app.jar

EXPOSE 8080

# JAVA_TOOL_OPTIONS is injected via docker-compose (includes -javaagent)
ENTRYPOINT ["java", "-jar", "app.jar"]
