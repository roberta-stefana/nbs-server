FROM openjdk:12-jdk-alpine
VOLUME /tmp
ARG JAR_FILE
COPY ${JAR_FILE} nbs-server-1.0-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "/nbs-server-1.0-SNAPSHOT.jar"]