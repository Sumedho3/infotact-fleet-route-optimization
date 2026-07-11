# =========================================================================
# STAGE 1: THE COMPILATION BRAIN (Build Environment)
# =========================================================================
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /build

# Copy the dependency layout first to optimize layer caching properties
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the actual source directory files
COPY src ./src

# Compile and package the production-ready executable jar file
RUN mvn clean package -DskipTests

# =========================================================================
# STAGE 2: THE RUNTIME ENGINE (Production Target Layer)
# =========================================================================
# Using the lightweight Eclipse Temurin base image requested by the track blueprint
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy only the final compiled artifact out of the builder stage matrix
COPY --from=builder /build/target/*.jar fleet-dispatch-service.jar

# Expose the standard servlet web traffic port
EXPOSE 8080

# Launch the microservice application engine
ENTRYPOINT ["java", "-jar", "fleet-dispatch-service.jar"]