# 🐳 Documentação Docker - Nota Fiscal Eletrônica

## 📋 Estrutura

O projeto está configurado para ser executado em containers Docker:

- **PostgreSQL 16** - Banco de dados
- **Spring Boot App** - Aplicação Java

## 🚀 Como Usar

### 1. **Compilar e Rodar com Docker Compose**

```bash
# Na raiz do projeto
docker-compose up --build
```

Isso irá:
- Compilar a imagem Spring Boot
- Iniciar o PostgreSQL
- Rodar a aplicação Spring Boot

### 2. **Acessar a Aplicação**

- **API REST**: http://localhost:5002
- **Swagger UI**: http://localhost:5002/swagger-ui.html
- **PostgreSQL**: localhost:5001

### 3. **Variáveis de Ambiente**

Configurar no arquivo `.env`:

```env
DB_USERNAME=nfe_java_admin
DB_PASSWORD=+!6Y;A8UJ-kSy+wLr<lgv79G%r
DB_NAME=nota_fiscal_java_db
DB_PORT=5001
DB_HOST=localhost
```

### 4. **Comandos Úteis**

```bash
# Parar containers
docker-compose down

# Parar e remover volumes (CUIDADO - apaga dados)
docker-compose down -v

# Ver logs da aplicação
docker-compose logs app -f

# Ver logs do banco
docker-compose logs postgres -f

# Entrar no container da aplicação
docker exec -it nfe_app bash

# Entrar no PostgreSQL
docker exec -it nfe_db psql -U nfe_java_admin -d nota_fiscal_java_db
```

### 5. **Verificar Migrações**

```bash
# Cliente psql
docker exec nfe_db psql -U nfe_java_admin -d nota_fiscal_java_db -c "\dt"

# Resultado esperado:
# flyway_schema_history
# notas_fiscais
# itens_notas_fiscais
```

## 🛠️ Build Manual da Imagem

```bash
cd notaFiscalEletronica/notaFiscalEletronica

# Build
docker build -t nfe-app:latest .

# Rodar
docker run -e DB_HOST=postgres \
           -e DB_PORT=5432 \
           -e DB_NAME=nota_fiscal_java_db \
           -e DB_USERNAME=nfe_java_admin \
           -e "DB_PASSWORD=+!6Y;A8UJ-kSy+wLr<lgv79G%r" \
           -p 8080:8080 \
           nfe-app:latest
```

## 📦 Estrutura do Dockerfile

```dockerfile
# Multi-stage build para otimizar tamanho
Stage 1: Build com Maven (3.9.12)
Stage 2: Runtime com Java 21 JRE Alpine (leve)
```

**Tamanho final**: ~150MB

## ✅ Health Checks

A aplicação possui health check automático:

```bash
# Verificar saúde
curl http://localhost:8080/actuator/health
```

## 🐛 Troubleshooting

### Container não inicia
```bash
# Ver logs
docker-compose logs app
docker-compose logs postgres
```

### Tabelas não aparecem
```bash
# Verificar se Flyway rodou
docker exec nfe_db psql -U nfe_java_admin -d nota_fiscal_java_db -c "SELECT * FROM flyway_schema_history;"
```

### Conectar no DBeaver
- Host: `localhost`
- Port: `5001`
- Database: `nota_fiscal_java_db`
- User: `nfe_java_admin`
- Password: (do .env)
