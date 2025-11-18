# Build step med Amazon Corretto
FROM maven:3.9.4-amazoncorretto-21 AS build
WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage: Amazon Corretto 21 Alpine
FROM amazoncorretto:21-alpine
WORKDIR /app
COPY --from=build /build/target/sentiment-docker-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]