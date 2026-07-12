# =========================================================================
# STAGE 1: THE COMPILATION BRAIN (Build Environment)
# =========================================================================
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /build

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests

# =========================================================================
# STAGE 2: THE RUNTIME ENGINE (Production Target Layer)
# =========================================================================
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# 🎯 SECURITY FIX: Create a dedicated, low-privilege system group and user
# - 'addgroup -S appgroup': Creates a system group named appgroup
# - 'adduser -S appuser -G appgroup': Creates a system user named appuser inside appgroup
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copy only the compiled jar file out of the builder stage matrix
COPY --from=builder /build/target/*.jar fleet-dispatch-service.jar

# 🎯 SECURITY FIX: Set ownership of the /app directory to our new non-root user
RUN chown -R appuser:appgroup /app

# Expose the standard servlet web traffic port
EXPOSE 8080

# 🎯 SECURITY FIX: Switch execution context from root to the low-privilege user
USER appuser

# Launch the microservice application engine safely
ENTRYPOINT ["java", "-jar", "fleet-dispatch-service.jar"]