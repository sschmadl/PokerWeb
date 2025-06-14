# --- Frontend Build Stage ---
FROM node:18-alpine AS frontend-build

# Set working directory
WORKDIR /app

# Copy package files and install dependencies
COPY Poker-frontend/package.json Poker-frontend/package-lock.json ./
RUN npm install

# Copy the rest of the frontend code
COPY Poker-frontend ./

# Build the frontend (ensure Nuxt generates static output)
RUN npx nuxi generate

# --- Backend Build Stage ---
FROM eclipse-temurin:17-jdk AS backend-build

WORKDIR /app
COPY Poker-Backend/pom.xml .
COPY Poker-Backend/mvnw .
COPY Poker-Backend/.mvn .mvn

# Cache dependencies here
RUN ./mvnw dependency:go-offline

COPY Poker-Backend/src ./src

COPY --from=frontend-build /app/.output/public ./src/main/resources/static

RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

# --- Final Stage ---
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy backend JAR file
COPY --from=backend-build /app/target/PokerBackend-0.0.1-SNAPSHOT.jar app.jar

# Copy frontend static files (Nuxt.js build output)
# The Nuxt static files generated by `nuxt generate` are typically placed in `.output/public`
#COPY --from=frontend-build /app/.output/public /static

# Expose the port for the Spring Boot application
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]