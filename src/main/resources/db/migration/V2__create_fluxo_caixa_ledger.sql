-- Migration V2: Fluxo de Caixa Soberano e Ledger de Operações

-- 1. Contas Financeiras
CREATE TABLE conta_financeira (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    saldo_atual DECIMAL(19,2) NOT NULL DEFAULT 0.00,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id)
);

-- 2. Categorias Financeiras (Plano de Contas)
CREATE TABLE categoria_financeira (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    tipo VARCHAR(30) NOT NULL,
    cor_hex VARCHAR(20) NULL,
    PRIMARY KEY (id)
);

-- 3. Transações Financeiras (Vetor Financeiro / Ledger Imutável)
CREATE TABLE transacao_financeira (
    id BIGINT NOT NULL AUTO_INCREMENT,
    codigo_transacao VARCHAR(50) NOT NULL UNIQUE,
    tipo_movimento VARCHAR(10) NOT NULL,
    tipo_evento VARCHAR(50) NOT NULL,
    descricao VARCHAR(255) NOT NULL,
    valor DECIMAL(19,2) NOT NULL,
    data_competencia DATETIME NOT NULL,
    data_liquidacao DATETIME NULL,
    status VARCHAR(20) NOT NULL,
    modulo_origem VARCHAR(50) NOT NULL,
    origem_id BIGINT NULL,
    conta_financeira_id BIGINT NOT NULL,
    categoria_id BIGINT NOT NULL,
    cpv_historico DECIMAL(19,2) NULL,
    margem_nominal DECIMAL(19,2) NULL,
    observacoes VARCHAR(500) NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_transacao_conta
        FOREIGN KEY (conta_financeira_id) REFERENCES conta_financeira (id),
    CONSTRAINT fk_transacao_categoria
        FOREIGN KEY (categoria_id) REFERENCES categoria_financeira (id)
);

CREATE INDEX idx_transacao_data_competencia
    ON transacao_financeira (data_competencia DESC);

CREATE INDEX idx_transacao_modulo_origem
    ON transacao_financeira (modulo_origem, origem_id);

-- 4. Ledger Físico de Estoque & Refugo (Vetor Físico)
CREATE TABLE ledger_estoque_movimento (
    id BIGINT NOT NULL AUTO_INCREMENT,
    codigo_evento VARCHAR(50) NOT NULL,
    tipo_operacao VARCHAR(50) NOT NULL,
    tipo_insumo VARCHAR(30) NOT NULL,
    variacao_volumetrica VARCHAR(50) NOT NULL,
    quantidade_delta DECIMAL(19,2) NOT NULL,
    impacto_detalhado VARCHAR(255) NOT NULL,
    referencia_matriz VARCHAR(255) NOT NULL,
    custo_unitario_aplicado DECIMAL(19,6) NOT NULL,
    custo_total_impactado DECIMAL(19,2) NOT NULL,
    data_hora DATETIME NOT NULL,
    PRIMARY KEY (id)
);

CREATE INDEX idx_ledger_fisico_data_hora
    ON ledger_estoque_movimento (data_hora DESC);

-- Carga Inicial (Seeds)
INSERT INTO conta_financeira (nome, tipo, saldo_atual, ativo) VALUES
('Caixa Operacional Principal', 'CAIXA_INTERNO', 0.00, TRUE),
('Conta Bancária / Gateway', 'BANCO', 0.00, TRUE);

INSERT INTO categoria_financeira (nome, tipo, cor_hex) VALUES
('Matéria-Prima (Blanks)', 'CUSTO_DIRETO', '#f85149'),
('Frete & Logística', 'CUSTO_LOGISTICO', '#d29922'),
('Matéria-Prima (DTF)', 'CUSTO_DIRETO', '#a371f7'),
('Custos Operacionais', 'CUSTO_OPERACIONAL', '#db61a2'),
('Receita de Vendas', 'RECEITA', '#2ea043'),
('Despesas Fixas', 'DESPESA_FIXA', '#f0883e'),
('Aporte de Capital', 'APORTE', '#58a6ff'),
('Investimentos', 'INVESTIMENTO', '#388bfd');
