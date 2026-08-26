CREATE TABLE lote_blank (
    id BIGINT NOT NULL AUTO_INCREMENT,
    data_entrada DATETIME NOT NULL,
    valor_frete_total DECIMAL(19,2) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE lote_blank_item (
    id BIGINT NOT NULL AUTO_INCREMENT,
    lote_blank_id BIGINT NOT NULL,
    sku_base VARCHAR(120) NOT NULL,
    tamanho VARCHAR(10) NOT NULL,
    quantidade_adquirida INT NOT NULL,
    quantidade_disponivel INT NOT NULL,
    preco_nota_unitario DECIMAL(19,2) NOT NULL,
    custo_com_frete_calculado DECIMAL(19,2) NOT NULL,
    foto_path VARCHAR(500) NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_lote_blank_item_lote_blank
        FOREIGN KEY (lote_blank_id) REFERENCES lote_blank (id)
);

CREATE INDEX idx_lote_blank_item_sku_tamanho_data
    ON lote_blank_item (sku_base, tamanho, lote_blank_id);

CREATE TABLE estoque_fluido_dtf (
    id BIGINT NOT NULL AUTO_INCREMENT,
    area_total_cm2 DECIMAL(19,2) NOT NULL,
    custo_total_acumulado DECIMAL(19,2) NOT NULL,
    preco_medio_por_cm2 DECIMAL(19,6) NOT NULL,
    atualizado_em DATETIME NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE entrada_dtf (
    id BIGINT NOT NULL AUTO_INCREMENT,
    estoque_fluido_dtf_id BIGINT NOT NULL,
    largura_cm DECIMAL(19,2) NOT NULL,
    comprimento_cm DECIMAL(19,2) NOT NULL,
    area_lote_cm2 DECIMAL(19,2) NOT NULL,
    valor_nota DECIMAL(19,2) NOT NULL,
    custo_logistico DECIMAL(19,2) NOT NULL,
    valor_total_gasto DECIMAL(19,2) NOT NULL,
    saldo_area_anterior_cm2 DECIMAL(19,2) NOT NULL,
    saldo_area_posterior_cm2 DECIMAL(19,2) NOT NULL,
    preco_medio_anterior_cm2 DECIMAL(19,6) NOT NULL,
    preco_medio_posterior_cm2 DECIMAL(19,6) NOT NULL,
    registrado_em DATETIME NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_entrada_dtf_estoque
        FOREIGN KEY (estoque_fluido_dtf_id) REFERENCES estoque_fluido_dtf (id)
);

CREATE TABLE ordem_producao (
    id BIGINT NOT NULL AUTO_INCREMENT,
    descricao_modelo VARCHAR(255) NOT NULL,
    lote_blank_item_id BIGINT NOT NULL,
    quantidade_produzir INT NOT NULL,
    operador_responsavel VARCHAR(120) NOT NULL,
    fator_aproveitamento_dtf_perc DECIMAL(5,2) NOT NULL,
    custo_blanks_total DECIMAL(19,2) NOT NULL,
    custo_dtf_total DECIMAL(19,2) NOT NULL,
    custo_operacional_total DECIMAL(19,2) NOT NULL,
    cpv_total DECIMAL(19,2) NOT NULL,
    cpv_unitario_final DECIMAL(19,2) NOT NULL,
    foto_mockup_path VARCHAR(500) NULL,
    registrada_em DATETIME NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_ordem_producao_lote_blank_item
        FOREIGN KEY (lote_blank_item_id) REFERENCES lote_blank_item (id)
);

CREATE TABLE ordem_producao_fragmento (
    id BIGINT NOT NULL AUTO_INCREMENT,
    ordem_producao_id BIGINT NOT NULL,
    descricao VARCHAR(255) NOT NULL,
    largura_cm DECIMAL(19,2) NOT NULL,
    altura_cm DECIMAL(19,2) NOT NULL,
    area_cm2 DECIMAL(19,2) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_ordem_producao_fragmento_ordem
        FOREIGN KEY (ordem_producao_id) REFERENCES ordem_producao (id)
);

CREATE TABLE ordem_producao_consumo_blank (
    id BIGINT NOT NULL AUTO_INCREMENT,
    ordem_producao_id BIGINT NOT NULL,
    lote_blank_item_id BIGINT NOT NULL,
    quantidade_consumida INT NOT NULL,
    custo_unitario_aplicado DECIMAL(19,2) NOT NULL,
    custo_total DECIMAL(19,2) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_ordem_producao_consumo_blank_ordem
        FOREIGN KEY (ordem_producao_id) REFERENCES ordem_producao (id),
    CONSTRAINT fk_ordem_producao_consumo_blank_item
        FOREIGN KEY (lote_blank_item_id) REFERENCES lote_blank_item (id)
);

CREATE TABLE ordem_producao_consumo_dtf (
    id BIGINT NOT NULL AUTO_INCREMENT,
    ordem_producao_id BIGINT NOT NULL,
    area_consumida_cm2 DECIMAL(19,2) NOT NULL,
    custo_medio_aplicado DECIMAL(19,6) NOT NULL,
    custo_total DECIMAL(19,2) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_ordem_producao_consumo_dtf_ordem
        FOREIGN KEY (ordem_producao_id) REFERENCES ordem_producao (id)
);