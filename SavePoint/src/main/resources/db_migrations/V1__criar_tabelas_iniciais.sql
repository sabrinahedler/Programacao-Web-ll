CREATE TABLE tb_jogo (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         titulo VARCHAR(255) NOT NULL,
                         descricao TEXT NOT NULL,
                         data_lancamento DATE NOT NULL,
                         imagem_capa VARCHAR(255),
                         classificacao_indicativa VARCHAR(255) NOT NULL,
                         desenvolvedora VARCHAR(255) NOT NULL,
                         genero VARCHAR(255) NOT NULL,
                         requisitos_minimos_id BIGINT,
                         resumo_avaliacao_id BIGINT NOT NULL
);

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

CREATE TABLE tb_oferta_jogo (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        preco_original DOUBLE NOT NULL,
                        preco_atual DOUBLE NOT NULL,
                        percentual_desconto FLOAT NOT NULL,
                        jogo_id BIGINT NOT NULL NOT NULL,
                        loja_id BIGINT NOT NULL NOT NULL,
                        CONSTRAINT fk_oferta_jogo FOREIGN KEY (jogo_id) REFERENCES tb_jogo(id),
                        CONSTRAINT fk_oferta_loja FOREIGN KEY (loja_id) REFERENCES tb_loja(id)
);

CREATE TABLE tb_historico (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        data DATETIME NOT NULL,
                        preco DOUBLE NOT NULL,
                        oferta_id BIGINT NOT NULL,
                        CONSTRAINT fk_historico_oferta FOREIGN KEY (oferta_id) REFERENCES tb_oferta_jogo(id) ON DELETE CASCADE
);

CREATE TABLE tb_loja (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        nome VARCHAR(255) NOT NULL,
                        url_loja VARCHAR(255) NOT NULL,
                        url_logo VARCHAR(255) NOT NULL
);

