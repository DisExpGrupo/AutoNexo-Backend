# Stage 1: build
FROM maven:3.9.3-eclipse-temurin-17 AS build
WORKDIR /app

# Copiar pom y código
COPY pom.xml .
COPY src ./src

# Construir el jar sin tests (opcional)
RUN mvn clean package -DskipTests

# Stage 2: runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Crear usuario no-root
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copiar jar del stage anterior
COPY --from=build /app/target/*.jar app.jar

# Puerto
EXPOSE ${PORT:-8080}

# Variables de entorno
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Healthcheck
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:${PORT:-8080}/actuator/health || exit 1

# Ejecutar la app
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar"]