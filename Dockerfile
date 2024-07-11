FROM openjdk:21

COPY build/libs/insurance-project.jar insurance-project.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}", "-jar", "/insurance-project.jar"]