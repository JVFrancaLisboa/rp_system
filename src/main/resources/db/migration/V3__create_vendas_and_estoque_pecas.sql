-- Migration V3: Controle de Estoque de Peças Prontas e Módulo de Vendas (Boca a Boca / Nuvemshop)

-- 1. Adiciona coluna de estoque disponível na ordem de produção
ALTER TABLE ordem_producao ADD COLUMN quantidade_disponivel INT NOT NULL DEFAULT 0;

-- Atualiza ordens pré-existentes para que o estoque inicial seja igual à quantidade produzida
UPDATE ordem_producao SET quantidade_disponivel = quantidade_produzir WHERE quantidade_disponivel = 0;

-- 2. Tabela de Vendas
CREATE TABLE venda (
    id BIGINT NOT NULL AUTO_INCREMENT,
    codigo_venda VARCHAR(60) NOT NULL UNIQUE,
    canal_venda VARCHAR(50) NOT NULL,
    forma_pagamento VARCHAR(50) NOT NULL,
    ordem_producao_id BIGINT NULL,
    descricao_produto VARCHAR(255) NOT NULL,
    sku_base VARCHAR(120) NULL,
    tamanho VARCHAR(20) NULL,
    quantidade INT NOT NULL,
    preco_venda_unitario DECIMAL(19,2) NOT NULL,
    valor_total DECIMAL(19,2) NOT NULL,
    cpv_unitario DECIMAL(19,2) NOT NULL,
    cpv_total DECIMAL(19,2) NOT NULL,
    lucro_bruto DECIMAL(19,2) NOT NULL,
    margem_percentual DECIMAL(7,2) NULL,
    data_venda DATETIME NOT NULL,
    nome_cliente VARCHAR(150) NULL,
    observacoes VARCHAR(500) NULL,
    conta_financeira_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_venda_ordem_producao
        FOREIGN KEY (ordem_producao_id) REFERENCES ordem_producao (id),
    CONSTRAINT fk_venda_conta_financeira
        FOREIGN KEY (conta_financeira_id) REFERENCES conta_financeira (id)
);

CREATE INDEX idx_venda_data_venda
    ON venda (data_venda DESC);

CREATE INDEX idx_venda_canal
    ON venda (canal_venda);
