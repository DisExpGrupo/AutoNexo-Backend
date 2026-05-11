# Stage 1: Build with JDK 25 + Maven
FROM eclipse-temurin:25-jdk-alpine AS build

RUN apk add --no-cache maven bash git openssh

WORKDIR /app

COPY pom.xml . 
COPY src ./src

RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

# Crear carpeta de logs antes de cambiar de usuario
RUN mkdir -p /app/logs

# Crear usuario no-root
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copiar jar compilado
COPY --from=build /app/target/*.jar app.jar

# Exponer puerto
EXPOSE ${PORT:-8080}

# Variables de entorno
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-Xmx512m -Xms256m -Dserver.port=${PORT:-8080}"

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:${PORT:-8080}/actuator/health || exit 1

# Ejecutar app
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar"]