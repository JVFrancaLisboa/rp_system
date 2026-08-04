package com.rpsystem.presentation.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class EntradaDtfCreateRequest {

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal largura;

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal comprimento;

    @NotNull
    @DecimalMin(value = "0.00")
    private BigDecimal valorNota;

    @NotNull
    @DecimalMin(value = "0.00")
    private BigDecimal custoLogistico;

    public BigDecimal getLargura() {
        return largura;
    }

    public void setLargura(BigDecimal largura) {
        this.largura = largura;
    }

    public BigDecimal getComprimento() {
        return comprimento;
    }

    public void setComprimento(BigDecimal comprimento) {
        this.comprimento = comprimento;
    }

    public BigDecimal getValorNota() {
        return valorNota;
    }

    public void setValorNota(BigDecimal valorNota) {
        this.valorNota = valorNota;
    }

    public BigDecimal getCustoLogistico() {
        return custoLogistico;
    }

    public void setCustoLogistico(BigDecimal custoLogistico) {
        this.custoLogistico = custoLogistico;
    }
}