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

EXPOSE 8080

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]