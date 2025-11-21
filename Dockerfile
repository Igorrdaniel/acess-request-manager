# Build
FROM maven:3.9.9-amazoncorretto-21 AS build
COPY src /app/src
COPY pom.xml /app
RUN mvn -f /app/pom.xml clean package -DskipTests

# Runtime
FROM amazoncorretto:21-alpine
COPY --from=build /app/target/access-request-manager-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]