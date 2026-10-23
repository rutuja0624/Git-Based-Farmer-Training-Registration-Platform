# ==============================================================================
# Dockerfile: Git-Based Farmer Training Registration Platform
# Architecture: Multi-Stage Build with Eclipse Temurin 21 JRE Alpine
# Author: Rutuja Kamale (Roll No: 23102A0028, Course: DevOps)
# ==============================================================================

# ------------------------------------------------------------------------------
# Stage 1: Build & Packaging (Compile & Package Fat JAR)
# ------------------------------------------------------------------------------
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder

WORKDIR /build

# Cache Maven dependencies layer
COPY pom.xml .
RUN mvn dependency:go-offline -B || true

# Copy source code and build executable fat JAR
COPY src src
RUN mvn clean package -DskipTests=true

# ------------------------------------------------------------------------------
# Stage 2: Hardened Runtime Container
# ------------------------------------------------------------------------------
FROM eclipse-temurin:21-jre-alpine AS runtime

# Metadata labels
LABEL maintainer="Rutuja Kamale <23102A0028>" \
      project="Git-Based Farmer Training Registration Platform" \
      version="1.0.0" \
      component="Web Application & API Server" \
      course="DevOps"

# Install curl/wget for container healthcheck probe
RUN apk update && apk add --no-cache curl wget

# Security: Create non-root system group and user (Least Privilege Principle)
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

# Copy executable jar from builder stage
COPY --from=builder /build/target/farmer-registration-platform-1.0.0.jar /app/app.jar

# Enforce secure ownership
RUN chown -R appuser:appgroup /app

# Run container as non-root user
USER appuser

# Expose web server HTTP port
EXPOSE 8085

# Container Environment Defaults
ENV SERVER_PORT=8085 \
    SPRING_PROFILES_ACTIVE=dev \
    JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

# Docker Healthcheck Probe using Spring Boot Actuator
HEALTHCHECK --interval=20s --timeout=5s --start-period=25s --retries=3 \
  CMD wget -q --spider http://localhost:8085/actuator/health || exit 1

# Launch Spring Boot Application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
