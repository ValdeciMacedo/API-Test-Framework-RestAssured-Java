# 🛠️ Guia de Setup — VSCode

Passo a passo completo para abrir, configurar e rodar o projeto no Visual Studio Code.

---

## 1. Extensões necessárias no VSCode

Instale as seguintes extensões antes de abrir o projeto:

| Extensão | ID | Para quê |
|---|---|---|
| Extension Pack for Java | `vscjava.vscode-java-pack` | Suporte completo a Java |
| Maven for Java | `vscjava.vscode-maven` | Rodar comandos Maven |
| Test Runner for Java | `vscjava.vscode-java-test` | Rodar testes pelo painel |
| Docker | `ms-azuretools.vscode-docker` | Gerenciar containers |
| GitLens | `eamodio.gitlens` | Histórico do Git |

**Como instalar:**
1. Abra o VSCode
2. Pressione `Ctrl+Shift+X`
3. Pesquise pelo nome e clique em **Install**

---

## 2. Abrir o projeto no VSCode

```bash
# No terminal, dentro da pasta do projeto:
code .
```

Ou: **File → Open Folder → selecione a pasta `api-test-framework-restassured`**

---

## 3. Configurar o Java 17

1. Pressione `Ctrl+Shift+P`
2. Digite: `Java: Configure Java Runtime`
3. Verifique se o Java 17 está configurado
4. Se não estiver: baixe em https://adoptium.net/

---

## 4. Configurar variáveis de ambiente locais

Crie um arquivo `.env` na raiz do projeto (já está no .gitignore):

```bash
BASE_URL=https://serverest.dev
DB_URL=jdbc:postgresql://localhost:5432/servetest_db
DB_USER=postgres
DB_PASSWORD=postgres
```

---

## 5. Configurar launch.json para debug

Crie `.vscode/launch.json`:

```json
{
  "version": "0.2.0",
  "configurations": [
    {
      "type": "java",
      "name": "Debug Tests",
      "request": "launch",
      "mainClass": "",
      "projectName": "api-test-framework-restassured",
      "env": {
        "BASE_URL": "https://serverest.dev",
        "DB_URL": "jdbc:postgresql://localhost:5432/servetest_db",
        "DB_USER": "postgres",
        "DB_PASSWORD": "postgres"
      }
    }
  ]
}
```

---

## 6. Subir o banco PostgreSQL

No terminal integrado do VSCode (`Ctrl+``):

```bash
cd docker
docker-compose up -d
```

Verificar se está rodando:
```bash
docker ps
```

---

## 7. Rodar os testes

### Via terminal:
```bash
# Todos os testes
mvn clean test

# Apenas uma classe de teste
mvn clean test -Dtest=AuthTests

# Apenas um método
mvn clean test -Dtest=AuthTests#shouldLoginWithValidCredentials

# Grupo de testes (por pasta)
mvn clean test -Dtest="com.valdeci.apitests.tests.users.*"
```

### Via painel do VSCode:
1. Clique no ícone de **Testing** (beaker) na barra lateral
2. Expanda a árvore de testes
3. Clique em ▶ para rodar um teste ou suite inteira
4. Clique com botão direito → **Debug Test** para debug

---

## 8. Gerar relatório Allure

```bash
# Gerar relatório
mvn allure:report

# Abrir no navegador automaticamente
mvn allure:serve
```

---

## 9. Parar o banco quando terminar

```bash
cd docker
docker-compose down
```

Para remover os dados também:
```bash
docker-compose down -v
```

---

## 10. Estrutura de pastas no Explorer do VSCode

Após abrir o projeto, a estrutura no Explorer deve aparecer assim:

```
📁 api-test-framework-restassured
├── 📁 .github/workflows
├── 📁 docker
├── 📁 src/test/java/com/valdeci/apitests
│   ├── 📁 config
│   ├── 📁 clients
│   ├── 📁 models
│   ├── 📁 tests
│   └── 📁 utils
├── 📁 src/test/resources/schemas
├── 📄 pom.xml
└── 📄 README.md
```

---

## Comandos rápidos — Referência

```bash
# Instalar dependências
mvn clean install -DskipTests

# Rodar todos os testes
mvn clean test

# Rodar testes específicos
mvn clean test -Dtest=UserTests

# Relatório Allure
mvn allure:serve

# Subir banco
docker-compose -f docker/docker-compose.yml up -d

# Derrubar banco
docker-compose -f docker/docker-compose.yml down
```
