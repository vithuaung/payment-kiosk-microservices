FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy the built fat JAR
COPY target/*.jar patient-service.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "patient-service.jar"]
