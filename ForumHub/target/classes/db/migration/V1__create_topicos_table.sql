-- Flyway migration: cria tabela topicos
CREATE TABLE IF NOT EXISTS topicos (
    id SERIAL PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    mensagem TEXT NOT NULL,
    autor VARCHAR(255) NOT NULL,
    curso VARCHAR(255) NOT NULL,
    data_criacao TIMESTAMP NOT NULL,
    CONSTRAINT uq_topicos_titulo_mensagem UNIQUE (titulo, mensagem)
);

