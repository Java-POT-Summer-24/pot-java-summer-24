FROM openjdk:21
EXPOSE 8080

ADD build/libs/insurance-project.jar insurance-project.jar

ENTRYPOINT ["java", "-jar", "/insurance-project"]