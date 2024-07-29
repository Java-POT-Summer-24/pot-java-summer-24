FROM openjdk:21-jdk-slim

WORKDIR /app

COPY . .

RUN ./gradlew build

CMD ["./gradlew", "bootRun"]