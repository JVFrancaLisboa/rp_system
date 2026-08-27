package com.rpsystem.domain.sales.model;

public enum CanalVenda {
    DIRETA_BOCA_A_BOCA("Boca a Boca / Direta"),
    NUVEMSHOP("Nuvemshop"),
    OUTRO("Outro");

    private final String descricao;

    CanalVenda(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
