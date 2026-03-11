-- Cria a tabela de tópicos com constraint única sobre (titulo, mensagem)
CREATE TABLE IF NOT EXISTS topicos (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(200) NOT NULL,
    mensagem TEXT NOT NULL,
    autor VARCHAR(100) NOT NULL,
    curso VARCHAR(100) NOT NULL,
    data_criacao TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now()
);

-- Índice único para evitar duplicação de tópicos com mesmo título e mensagem
CREATE UNIQUE INDEX IF NOT EXISTS ux_topicos_titulo_mensagem ON topicos (titulo, mensagem);

