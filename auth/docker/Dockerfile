FROM openjdk:8-jre-alpine
MAINTAINER jaracks <jaracks@outlook.com>
COPY auth-gateway*.jar app.jar
EXPOSE 20100
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]