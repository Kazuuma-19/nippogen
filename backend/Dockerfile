FROM openjdk:21

WORKDIR /app

COPY build/libs/backend-0.0.1-SNAPSHOT.jar /app/nippogen-backend.jar

ENTRYPOINT ["java", "-jar", "/app/nippogen-backend.jar"]

EXPOSE 8080