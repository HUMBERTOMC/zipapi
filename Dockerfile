FROM openjdk:8-jdk-alpine
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} zip-api.jar
USER root
RUN mkdir /downloads
ENTRYPOINT ["java","-jar","/zip-api.jar"]