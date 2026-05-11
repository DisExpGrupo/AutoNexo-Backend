# Stage 1: Build the JAR
FROM maven:3.9.3-eclipse-temurin-25 AS build
WORKDIR /app

# Copiar pom y código fuente
COPY pom.xml .
COPY src ./src

# Construir el JAR
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

# Crear usuario no root
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copiar el JAR desde la etapa de build
COPY --from=build /app/target/backend-0.0.1-SNAPSHOT.jar app.jar

EXPOSE ${PORT:-8080}

ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-Xmx512m -Xms256m"

HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:${PORT:-8080}/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar"]