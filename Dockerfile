# Stage 1: Build
FROM eclipse-temurin:25-jdk-alpine AS build

RUN apk add --no-cache maven bash git openssh

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

# Crear carpeta de logs y dar permisos
RUN mkdir -p /app/logs \
    && addgroup -S spring \
    && adduser -S spring -G spring \
    && chown -R spring:spring /app/logs /app

USER spring:spring

COPY --from=build /app/target/*.jar app.jar

EXPOSE ${PORT:-8080}

ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-Xmx512m -Xms256m -Dserver.port=${PORT:-8080}"

HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:${PORT:-8080}/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar"]