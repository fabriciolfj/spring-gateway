FROM adoptopenjdk:11-jre-hotspot

ARG JAR_FILE=/build/libs/*.jar

COPY ${JAR_FILE} edge-service.jar

ENTRYPOINT ["java", "-jar", "edge-service.jar"]