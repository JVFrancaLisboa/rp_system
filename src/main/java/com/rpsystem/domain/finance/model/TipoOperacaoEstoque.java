package com.rpsystem.domain.finance.model;

public enum TipoOperacaoEstoque {
    INGESTAO_LOTE_BLANK("Ingestão de Blanks"),
    INGESTAO_LOTE_DTF("Ingestão de DTF"),
    BAIXA_MANUFATURA_BLANK("Baixa de Manufatura (Blank)"),
    BAIXA_MANUFATURA_DTF("Baixa de Manufatura (DTF)"),
    REFUGO_DTF("Refugo / Ineficiência de Corte"),
    AJUSTE_ESTOQUE("Ajuste de Estoque");

    private final String descricao;

    TipoOperacaoEstoque(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
