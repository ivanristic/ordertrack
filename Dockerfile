#FROM arm64v8/maven:3.6-openjdk-11-slim as build
FROM maven:3.6-openjdk-11-slim as build
RUN mkdir /ordertrack
COPY . /ordertrack
WORKDIR /ordertrack
RUN mvn clean -DskipTests package

#FROM arm64v8/openjdk:11-jre-slim
FROM localhost:5000/openjdk-with-chromium:latest
RUN mkdir /ordertrack
WORKDIR /ordertrack
COPY --from=build /ordertrack/target/*.jar app.jar
EXPOSE 8080
CMD ["java","-jar","app.jar"]
