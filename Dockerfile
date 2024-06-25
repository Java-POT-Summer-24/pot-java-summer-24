FROM openjdk:21
EXPOSE 8080

COPY build/libs/insurance-project.jar insurance-project.jar

ENTRYPOINT ["java", "-jar", "/insurance-project"]