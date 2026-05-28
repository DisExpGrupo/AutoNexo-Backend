# ==========================================
# ETAPA 1: Compilación (Build Stage)
# ==========================================
# Usamos una imagen con JDK para compilar
FROM eclipse-temurin:25-jdk-alpine AS builder
WORKDIR /build

# Copiamos los archivos de configuración de Maven y el código fuente
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
COPY src ./src

# Le damos permisos de ejecución al wrapper (por si acaso) y compilamos
RUN chmod +x mvnw && ./mvnw clean package -DskipTests

# ==========================================
# ETAPA 2: Ejecución (Mismo Runtime Stage que tienes)
# ==========================================
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

# Crear usuario no-root
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# ¡CLAVE!: En lugar de buscar en tu máquina local, 
# copiamos el .jar generado en la ETAPA 1 ("builder")
COPY --from=builder /build/target/*.jar app.jar

EXPOSE ${PORT:-8080}

ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-Xmx512m -Xms256m"

HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:${PORT:-8080}/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar"]
