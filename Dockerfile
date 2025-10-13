FROM openjdk:17-jdk-slim

RUN adduser --system --group spring
USER spring:spring

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]