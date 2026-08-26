package com.rpsystem.domain.finance.model;

public enum TipoEventoFinanceiro {
    COMPRA_BLANKS("Compra de Blanks"),
    FRETE_BLANKS("Frete de Blanks"),
    COMPRA_DTF("Compra de DTF"),
    FRETE_DTF("Custo Logístico DTF"),
    VENDA_PRODUTO("Receita de Venda"),
    CUSTO_OPERACIONAL("Custo Operacional"),
    DESPESA_FIXA("Despesa Fixa"),
    DESPESA_MANUAL("Despesa Manual"),
    APORTE("Aporte de Capital"),
    RETIRADA("Retirada / Pró-Labore");

    private final String descricao;

    TipoEventoFinanceiro(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
