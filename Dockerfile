#FROM arm64v8/maven:3.6-openjdk-11-slim as build
FROM maven:3.6-openjdk-11-slim as build
RUN mkdir /ordertrack
COPY . /ordertrack
WORKDIR /ordertrack
RUN mvn clean -DskipTests package

#FROM arm64v8/openjdk:11-jre-slim
FROM openjdk:11-jre-slim
RUN apt-get update && \
    apt-get install -y chromium --no-install-recommends && \
    apt-get remove --purge --auto-remove -y curl && \
    rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/* /var/cache/apt/*
#ENV WEBDRIVER_PATH=/usr/bin/chromedriver
RUN mkdir /ordertrack
WORKDIR /ordertrack
COPY --from=build /ordertrack/target/*.jar app.jar
EXPOSE 8080
CMD ["java","-jar","app.jar"]