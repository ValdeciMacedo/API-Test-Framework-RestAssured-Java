-- ============================================================
-- Script de inicialização do banco de dados para testes
-- ServeRest API Test Framework
-- ============================================================

CREATE TABLE IF NOT EXISTS usuarios (
    id          VARCHAR(50) PRIMARY KEY,
    nome        VARCHAR(255) NOT NULL,
    email       VARCHAR(255) UNIQUE NOT NULL,
    administrador VARCHAR(10) NOT NULL,
    criado_em   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS produtos (
    id          VARCHAR(50) PRIMARY KEY,
    nome        VARCHAR(255) NOT NULL,
    preco       INTEGER NOT NULL,
    descricao   TEXT,
    quantidade  INTEGER NOT NULL,
    criado_em   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS test_logs (
    id          SERIAL PRIMARY KEY,
    test_name   VARCHAR(255),
    status      VARCHAR(50),
    executado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Índices para performance nas queries de teste
CREATE INDEX IF NOT EXISTS idx_usuarios_email ON usuarios(email);
CREATE INDEX IF NOT EXISTS idx_produtos_nome ON produtos(nome);

-- Comentários para documentação
COMMENT ON TABLE usuarios IS 'Espelho de usuários criados via API para validação de persistência';
COMMENT ON TABLE produtos IS 'Espelho de produtos criados via API para validação de persistência';
COMMENT ON TABLE test_logs IS 'Log de execuções de testes automatizados';
