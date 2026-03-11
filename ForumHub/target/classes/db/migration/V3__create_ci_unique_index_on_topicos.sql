-- Garante unicidade case-insensitive sobre titulo + mensagem
-- Remove índice único anterior (se existir) e cria um índice único sobre lower(titulo), lower(mensagem)
DROP INDEX IF EXISTS ux_topicos_titulo_mensagem;

CREATE UNIQUE INDEX ux_topicos_titulo_mensagem_ci ON topicos (lower(titulo), lower(mensagem));

