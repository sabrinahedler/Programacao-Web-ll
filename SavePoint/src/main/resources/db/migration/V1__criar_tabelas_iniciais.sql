-- SavePoint: criacao das tabelas (MySQL 8).
-- As tabelas sem dependencias vem primeiro; as demais referenciam as anteriores por chave estrangeira.

-- Loja onde os jogos sao vendidos.
CREATE TABLE tb_loja (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         nome VARCHAR(255) NOT NULL,
                         url_loja VARCHAR(255) NOT NULL,
                         url_logo VARCHAR(255) NOT NULL
);

-- Requisitos minimos de hardware de um jogo.
CREATE TABLE tb_requisitos_minimos (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          processador VARCHAR(255) NOT NULL,
                          memoria VARCHAR(255) NOT NULL,
                          placa_de_video VARCHAR(255) NOT NULL,
                          sistema_operacional VARCHAR(255) NOT NULL
);

-- Usuario do sistema.
CREATE TABLE tb_usuario (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          nome VARCHAR(255) NOT NULL,
                          email VARCHAR(255) NOT NULL UNIQUE,
                          senha VARCHAR(255) NOT NULL,
                          telefone VARCHAR(50),
                          data_cadastro DATETIME NOT NULL,
                          ultimo_acesso DATETIME,
                          ativo BOOLEAN NOT NULL DEFAULT TRUE
);

-- Resumo consolidado das avaliacoes (a tabela tb_jogo aponta para ele).
CREATE TABLE tb_resumo_avaliacao (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          nota_media DOUBLE NOT NULL DEFAULT 0,
                          total_avaliacoes INT NOT NULL DEFAULT 0,
                          percentual_recomendacao DOUBLE NOT NULL DEFAULT 0,
                          resumo_gerado_ia TEXT,
                          data_geracao DATE
);

-- Jogo do catalogo, com requisitos minimos e resumo opcionais (relacionamentos 1:1).
CREATE TABLE tb_jogo (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         titulo VARCHAR(255) NOT NULL,
                         descricao TEXT NOT NULL,
                         data_lancamento DATE NOT NULL,
                         imagem_capa VARCHAR(255),
                         classificacao_indicativa VARCHAR(255) NOT NULL,
                         desenvolvedora VARCHAR(255) NOT NULL,
                         genero VARCHAR(255) NOT NULL,
                         requisitos_minimos_id BIGINT UNIQUE,
                         resumo_avaliacao_id BIGINT UNIQUE,
                         CONSTRAINT fk_jogo_requisitos FOREIGN KEY (requisitos_minimos_id) REFERENCES tb_requisitos_minimos(id),
                         CONSTRAINT fk_jogo_resumo FOREIGN KEY (resumo_avaliacao_id) REFERENCES tb_resumo_avaliacao(id)
);

-- Oferta de um jogo em uma loja.
CREATE TABLE tb_oferta_jogo (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         preco_original DOUBLE NOT NULL,
                         preco_atual DOUBLE NOT NULL,
                         percentual_desconto FLOAT NOT NULL,
                         jogo_id BIGINT NOT NULL,
                         loja_id BIGINT NOT NULL,
                         CONSTRAINT fk_oferta_jogo FOREIGN KEY (jogo_id) REFERENCES tb_jogo(id) ON DELETE CASCADE,
                         CONSTRAINT fk_oferta_loja FOREIGN KEY (loja_id) REFERENCES tb_loja(id) ON DELETE CASCADE
);

-- Historico de precos de uma oferta.
CREATE TABLE tb_historico (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         data DATETIME NOT NULL,
                         preco DOUBLE NOT NULL,
                         oferta_id BIGINT NOT NULL,
                         CONSTRAINT fk_historico_oferta FOREIGN KEY (oferta_id) REFERENCES tb_oferta_jogo(id) ON DELETE CASCADE
);

-- Avaliacao de um usuario sobre um jogo (nota de 1 a 5).
CREATE TABLE tb_avaliacao_usuario (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         nota INT NOT NULL,
                         texto_avaliacao TEXT,
                         data_publicacao DATETIME NOT NULL,
                         curtidas INT NOT NULL DEFAULT 0,
                         horas_jogadas INT NOT NULL DEFAULT 0,
                         indice_recomendacao BOOLEAN NOT NULL DEFAULT FALSE,
                         usuario_id BIGINT NOT NULL,
                         jogo_id BIGINT NOT NULL,
                         CONSTRAINT ck_avaliacao_nota CHECK (nota BETWEEN 1 AND 5),
                         CONSTRAINT fk_avaliacao_usuario FOREIGN KEY (usuario_id) REFERENCES tb_usuario(id) ON DELETE CASCADE,
                         CONSTRAINT fk_avaliacao_jogo FOREIGN KEY (jogo_id) REFERENCES tb_jogo(id) ON DELETE CASCADE
);

-- Lista de desejos de um usuario (relacionamento 1:1).
CREATE TABLE tb_lista_desejos (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         usuario_id BIGINT NOT NULL UNIQUE,
                         CONSTRAINT fk_lista_usuario FOREIGN KEY (usuario_id) REFERENCES tb_usuario(id) ON DELETE CASCADE
);

-- Jogo adicionado a uma lista de desejos, sem repeticao.
CREATE TABLE tb_item_lista_desejos (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         preco_alerta DOUBLE NOT NULL DEFAULT 0,
                         notificar_oferta BOOLEAN NOT NULL DEFAULT TRUE,
                         lista_desejos_id BIGINT NOT NULL,
                         jogo_id BIGINT NOT NULL,
                         CONSTRAINT uq_item_lista_jogo UNIQUE (lista_desejos_id, jogo_id),
                         CONSTRAINT fk_item_lista FOREIGN KEY (lista_desejos_id) REFERENCES tb_lista_desejos(id) ON DELETE CASCADE,
                         CONSTRAINT fk_item_jogo FOREIGN KEY (jogo_id) REFERENCES tb_jogo(id) ON DELETE CASCADE
);