#FROM arm64v8/maven:3.6-openjdk-11-slim as build
FROM maven:3.8.5-openjdk-17-slim as build
RUN mkdir /ordertrack
COPY . /ordertrack
WORKDIR /ordertrack
RUN mvn clean -DskipTests package

#FROM arm64v8/openjdk:11-jre-slim
FROM localhost:5000/openjdk:17-jdk-slim-chromium-with-driver-109
RUN mkdir /ordertrack
WORKDIR /ordertrack
COPY --from=build /ordertrack/target/*.jar app.jar
EXPOSE 8080
CMD ["java","-jar","app.jar"]
