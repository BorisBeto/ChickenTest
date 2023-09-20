FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY target/demoChickenTest-0.0.1-SNAPSHOT.jar chickentest-app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "chickentest-app.jar"]