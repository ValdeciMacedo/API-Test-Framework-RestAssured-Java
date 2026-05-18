# 🧪 API Test Framework — RestAssured + Java + PostgreSQL

[![CI/CD](https://github.com/ValdeciMacedo/api-test-framework-restassured/actions/workflows/api-tests.yml/badge.svg)](https://github.com/ValdeciMacedo/api-test-framework-restassured/actions)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://adoptium.net/)
[![RestAssured](https://img.shields.io/badge/RestAssured-5.4.0-green.svg)](https://rest-assured.io/)
[![JUnit](https://img.shields.io/badge/JUnit-5-blue.svg)](https://junit.org/junit5/)
[![Allure](https://img.shields.io/badge/Allure-Report-yellow.svg)](https://allurereport.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue.svg)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-Compose-blue.svg)](https://www.docker.com/)

---

## 📋 Sobre o Projeto

Framework de automação de testes de API construído com **RestAssured + Java 17**, cobrindo o ciclo completo de qualidade de APIs REST: autenticação, CRUD, validação de contrato (JSON Schema), cenários negativos, fluxos encadeados e validação direta no banco de dados **PostgreSQL**.

API sob teste: **[ServeRest](https://serverest.dev)** — plataforma que simula um e-commerce com endpoints de usuários, produtos e carrinhos.

---

## 🏗️ Arquitetura do Framework

```
api-test-framework-restassured/
│
├── .github/
│   └── workflows/
│       └── api-tests.yml          # Pipeline CI/CD GitHub Actions
│
├── docker/
│   ├── docker-compose.yml         # PostgreSQL em container
│   └── init.sql                   # Script de inicialização do banco
│
├── src/test/
│   ├── java/com/valdeci/apitests/
│   │   ├── config/
│   │   │   ├── ApiConfig.java         # Configuração central do RestAssured
│   │   │   ├── DatabaseConfig.java    # Conexão e queries PostgreSQL
│   │   │   └── EnvConfig.java         # Variáveis de ambiente / CI/CD
│   │   │
│   │   ├── clients/
│   │   │   ├── AuthClient.java        # Abstração dos endpoints de auth
│   │   │   ├── UserClient.java        # Abstração dos endpoints de usuários
│   │   │   └── ProductClient.java     # Abstração dos endpoints de produtos
│   │   │
│   │   ├── models/
│   │   │   ├── User.java              # POJO de usuário
│   │   │   ├── Product.java           # POJO de produto
│   │   │   └── AuthToken.java         # POJO de resposta de autenticação
│   │   │
│   │   ├── tests/
│   │   │   ├── auth/
│   │   │   │   └── AuthTests.java     # Testes de login e token
│   │   │   ├── users/
│   │   │   │   └── UserTests.java     # CRUD completo + validação banco
│   │   │   └── products/
│   │   │       └── ProductTests.java  # CRUD completo + auth
│   │   │
│   │   └── utils/
│   │       ├── DataFactory.java       # Geração de massa com JavaFaker
│   │       └── SchemaValidator.java   # Validação de contrato JSON Schema
│   │
│   └── resources/
│       └── schemas/
│           ├── users-list-schema.json     # Contrato da lista de usuários
│           └── products-list-schema.json  # Contrato da lista de produtos
│
├── pom.xml                            # Dependências Maven
└── README.md
```

---

## 🔧 Tech Stack

| Tecnologia | Versão | Finalidade |
|---|---|---|
| Java | 17 | Linguagem base |
| RestAssured | 5.4.0 | Testes de API REST |
| JUnit 5 | 5.10.2 | Framework de testes |
| Allure Report | 2.25.0 | Relatórios visuais |
| Jackson | 2.17.0 | Serialização JSON |
| PostgreSQL | 15 | Validação de persistência |
| JavaFaker | 1.0.2 | Geração de massa de dados |
| Docker Compose | — | Ambiente isolado |
| GitHub Actions | — | CI/CD |

---

## 🧪 Cobertura de Testes

### Autenticação
- ✅ Login com credenciais válidas e validação do token Bearer
- ✅ Reaproveitamento de token em requests autenticados
- ✅ Login com senha inválida → 401
- ✅ Login com email inexistente → 401
- ✅ Login com campos vazios → 400

### Usuários
- ✅ Criar usuário → 201 + validação de ID
- ✅ Criar usuário com email duplicado → 400
- ✅ Criar usuário sem campos obrigatórios → 400
- ✅ Listar usuários + validação de JSON Schema
- ✅ Buscar por ID → 200
- ✅ Buscar com ID inexistente → 400
- ✅ Atualizar usuário autenticado → 200
- ✅ Atualizar sem token → 401
- ✅ Deletar usuário → 200
- ✅ **Validar exclusão diretamente no PostgreSQL**
- ✅ Fluxo completo (criar → buscar → atualizar → deletar → confirmar)

### Produtos
- ✅ Criar produto com token admin → 201
- ✅ Criar sem autenticação → 401
- ✅ Listar produtos + validação de JSON Schema
- ✅ Buscar por ID → 200
- ✅ Buscar com ID inexistente → 400
- ✅ Atualizar produto → 200
- ✅ Deletar produto → 200
- ✅ Fluxo completo de produto

---

## 🚀 Como Rodar Localmente

### Pré-requisitos
- Java 17+
- Maven 3.8+
- Docker e Docker Compose

### 1. Clone o repositório

```bash
git clone https://github.com/ValdeciMacedo/api-test-framework-restassured.git
cd api-test-framework-restassured
```

### 2. Suba o banco de dados PostgreSQL

```bash
cd docker
docker-compose up -d
cd ..
```

Aguarde o container estar saudável:
```bash
docker ps
# STATUS deve ser: Up X seconds (healthy)
```

### 3. Execute os testes

```bash
mvn clean test
```

### 4. Gere e abra o relatório Allure

```bash
mvn allure:report
mvn allure:serve
```

O relatório abrirá automaticamente no navegador em `http://localhost:PORT`.

---

## ⚙️ Configuração por Ambiente

Você pode sobrescrever as configurações via variáveis de ambiente ou system properties:

```bash
# Rodar contra outro ambiente
mvn clean test -Dbase.url=https://outro-ambiente.dev

# Variáveis de banco
export DB_URL=jdbc:postgresql://localhost:5432/servetest_db
export DB_USER=postgres
export DB_PASSWORD=postgres
```

---

## 🔄 CI/CD — GitHub Actions

O pipeline executa automaticamente em:
- **Push** para `main` ou `develop`
- **Pull Requests** para `main`
- **Agendamento diário** às 6h (regressão completa)

O relatório Allure é publicado automaticamente no **GitHub Pages** a cada execução na `main`.

---

## 📊 Relatório Allure

O Allure Report exibe:
- Suites organizadas por Epic → Feature → Story
- Detalhes de request e response de cada chamada
- Status de cada cenário (passed/failed/broken)
- Histórico de execuções
- Logs e screenshots de falha

---

## 👤 Autor

**Valdeci Macedo**
Senior QA Engineer | SDET | Automation E2E · API · Performance

[![LinkedIn](https://img.shields.io/badge/LinkedIn-valdecimacedo-blue?logo=linkedin)](https://www.linkedin.com/in/valdecimacedo/)
[![GitHub](https://img.shields.io/badge/GitHub-ValdeciMacedo-black?logo=github)](https://github.com/ValdeciMacedo)
