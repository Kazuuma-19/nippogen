FROM openjdk:21

WORKDIR /app

COPY build/libs/backend-0.0.1-SNAPSHOT.jar /app/valet-ai-backend.jar

ENTRYPOINT ["java", "-jar", "/app/valet-ai-backend.jar"]

EXPOSE 8080