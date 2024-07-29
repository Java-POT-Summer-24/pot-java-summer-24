FROM openjdk:21-jdk-slim

WORKDIR /app

COPY . .

RUN ./gradlew build

EXPOSE 8082

CMD ["./gradlew", "bootRun"]