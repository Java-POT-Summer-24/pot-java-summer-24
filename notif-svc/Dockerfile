FROM openjdk:21-jdk-slim

COPY build/libs/notification-microservice-0.0.1-SNAPSHOT.jar notification-microservice.jar

EXPOSE 8082

ENTRYPOINT ["java", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}", "-jar", "/notification-microservice.jar"]
