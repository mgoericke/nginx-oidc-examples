FROM maven:3.8-amazoncorretto-17 as build
WORKDIR /workspace/app
COPY pom.xml .
COPY src src

RUN mvn clean package -DskipTests

FROM amazoncorretto:17-alpine-jdk
COPY --from=build /workspace/app/target/*.jar /app/demo-oidc.jar
WORKDIR /app
ENTRYPOINT ["java","-jar","demo-oidc.jar"]