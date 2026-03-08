# Build stage
FROM maven:3.9.12-eclipse-temurin-21-alpine AS builder

WORKDIR /app

# Copiar arquivos pom e source
COPY pom.xml .
COPY src ./src

# Build da aplicação
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Instalar curl para health check
RUN apk add --no-cache curl

# Copiar JAR do builder
COPY --from=builder /app/target/*.jar app.jar

# Variáveis de ambiente padrão
ENV DB_HOST=postgres \
    DB_PORT=5432 \
    DB_NAME=nota_fiscal_java_db \
    DB_USERNAME=nfe_java_admin \
    DB_PASSWORD=password

# Expor porta
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Executar aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]

