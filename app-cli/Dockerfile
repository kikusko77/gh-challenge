FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn -pl app-cli -am clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/app-cli/target/app-cli-1.0.0.jar app-cli.jar

ENV SPRING_PROFILES_ACTIVE=local

ENTRYPOINT ["java", "-jar", "app-cli.jar"]
