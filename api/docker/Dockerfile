FROM openjdk:8-jre-alpine
MAINTAINER jaracks <jaracks@outlook.com>
COPY api-gateway*.jar app.jar
EXPOSE 20200
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]