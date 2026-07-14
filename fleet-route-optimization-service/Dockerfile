# =========================================================================
# STAGE 1: THE COMPILATION BRAIN (Build Environment)
# =========================================================================
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /build

COPY pom.xml .

# 🎯 DAY 3 CACHING FIX: Added cache mount flag to keep dependencies cached locally
RUN --mount=type=cache,target=/root/.m2 mvn dependency:go-offline -B

COPY src ./src

# 🎯 DAY 3 CACHING FIX: Added cache mount flag to reuse dependencies during packaging
RUN --mount=type=cache,target=/root/.m2 mvn clean package -DskipTests

# =========================================================================
# STAGE 2: THE RUNTIME ENGINE (Production Target Layer)
# =========================================================================
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# 🎯 SECURITY FIX (Day 2): Create a dedicated, low-privilege system group and user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copy only the compiled jar file out of the builder stage matrix
COPY --from=builder /build/target/*.jar fleet-dispatch-service.jar

# 🎯 SECURITY FIX (Day 2): Set ownership of the /app directory to our new non-root user
RUN chown -R appuser:appgroup /app

# Expose the standard servlet web traffic port
EXPOSE 8080

# 🎯 SECURITY FIX (Day 2): Switch execution context from root to the low-privilege user
USER appuser

# Launch the microservice application engine safely using the docker environment profile
ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar", "fleet-dispatch-service.jar"]