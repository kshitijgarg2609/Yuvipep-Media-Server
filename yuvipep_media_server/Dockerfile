#
# Build stage
#
FROM maven:3.6.0-jdk-11-slim AS build
COPY src /home/app/src
COPY pom.xml /home/app
# COPY application.yml /home/app 
RUN mvn -f /home/app/pom.xml clean install package -DskipTests

#
# Package stage
#
FROM openjdk:11-jre-slim
COPY --from=build /home/app/target/yuvipep_media_server-0.0.1-SNAPSHOT.jar /usr/local/lib/yuvipep_media_server.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/local/lib/yuvipep_media_server.jar"]