CREATE TABLE IF NOT EXISTS perfil (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS usuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS perfil_usario (
    usuario_id BIGINT NOT NULL,
    perfil_id BIGINT NOT NULL,
    PRIMARY KEY (usuario_id, perfil_id),
    CONSTRAINT fk_usuario_perfil_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE,
    CONSTRAINT fk_usuario_perfil_perfil FOREIGN KEY (perfil_id) REFERENCES perfil(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS fornecedor (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email_contato VARCHAR(100),
    telefone VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS especificacao (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    autonomia_km INT,
    capacidade_bateria_kwh DOUBLE,
    potencia_cv INT,
    tempo_carga_horas DOUBLE
);

CREATE TABLE IF NOT EXISTS veiculo (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    marca VARCHAR(50) NOT NULL,
    modelo VARCHAR(50) NOT NULL,
    carroceria VARCHAR(50),
    preco DOUBLE,
    quantidade_estoque INT DEFAULT 0,
    especificacao_id BIGINT UNIQUE,
    fornecedor_id BIGINT,
    CONSTRAINT fk_veiculo_especificacao FOREIGN KEY (especificacao_id) REFERENCES tb_especificacao(id) ON DELETE SET NULL,
    CONSTRAINT fk_veiculo_fornecedor FOREIGN KEY (fornecedor_id) REFERENCES tb_fornecedor(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS proposta (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome_visitante VARCHAR(100) NOT NULL,
    email_visitante VARCHAR(100) NOT NULL,
    telefone_visitante VARCHAR(20),
    tipo_solicitacao VARCHAR(30) NOT NULL,
    data_solicitacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) DEFAULT 'PENDENTE',
    veiculo_id BIGINT NOT NULL,
    CONSTRAINT fk_proposta_veiculo FOREIGN KEY (veiculo_id) REFERENCES tb_veiculo(id) ON DELETE CASCADE
);