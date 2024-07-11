FROM openjdk:21

COPY build/libs/insurance-project.jar insurance-project.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/insurance-project.jar"]