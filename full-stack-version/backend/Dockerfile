# Stage 1: Build with Maven
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run with Java
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/personal-diary-backend-1.0-jar-with-dependencies.jar app.jar

# Create backups directory for File Handling feature
RUN mkdir -p /app/backups

EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
