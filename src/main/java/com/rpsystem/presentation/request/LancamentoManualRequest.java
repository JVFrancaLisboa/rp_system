package com.rpsystem.presentation.request;

import com.rpsystem.domain.finance.model.TipoEventoFinanceiro;
import com.rpsystem.domain.finance.model.TipoMovimento;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class LancamentoManualRequest {

    @NotNull
    private TipoMovimento tipoMovimento;

    @NotNull
    private TipoEventoFinanceiro tipoEvento;

    @NotBlank
    private String descricao;

    @NotNull
    @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero.")
    private BigDecimal valor;

    private Long contaId;

    private Long categoriaId;

    private BigDecimal cpvHistorico;

    private String observacoes;

    public TipoMovimento getTipoMovimento() {
        return tipoMovimento;
    }

    public void setTipoMovimento(TipoMovimento tipoMovimento) {
        this.tipoMovimento = tipoMovimento;
    }

    public TipoEventoFinanceiro getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(TipoEventoFinanceiro tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Long getContaId() {
        return contaId;
    }

    public void setContaId(Long contaId) {
        this.contaId = contaId;
    }

    public Long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }

    public BigDecimal getCpvHistorico() {
        return cpvHistorico;
    }

    public void setCpvHistorico(BigDecimal cpvHistorico) {
        this.cpvHistorico = cpvHistorico;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}
