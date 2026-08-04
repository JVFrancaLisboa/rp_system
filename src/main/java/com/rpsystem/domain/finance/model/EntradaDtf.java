package com.rpsystem.domain.finance.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "entrada_dtf")
public class EntradaDtf {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "largura_cm", nullable = false, precision = 19, scale = 2)
    private BigDecimal larguraCm;

    @Column(name = "comprimento_cm", nullable = false, precision = 19, scale = 2)
    private BigDecimal comprimentoCm;

    @Column(name = "area_lote_cm2", nullable = false, precision = 19, scale = 2)
    private BigDecimal areaLoteCm2;

    @Column(name = "valor_nota", nullable = false, precision = 19, scale = 2)
    private BigDecimal valorNota;

    @Column(name = "custo_logistico", nullable = false, precision = 19, scale = 2)
    private BigDecimal custoLogistico;

    @Column(name = "valor_total_gasto", nullable = false, precision = 19, scale = 2)
    private BigDecimal valorTotalGasto;

    @Column(name = "saldo_area_anterior_cm2", nullable = false, precision = 19, scale = 2)
    private BigDecimal saldoAreaAnteriorCm2;

    @Column(name = "saldo_area_posterior_cm2", nullable = false, precision = 19, scale = 2)
    private BigDecimal saldoAreaPosteriorCm2;

    @Column(name = "preco_medio_anterior_cm2", nullable = false, precision = 19, scale = 6)
    private BigDecimal precoMedioAnteriorCm2;

    @Column(name = "preco_medio_posterior_cm2", nullable = false, precision = 19, scale = 6)
    private BigDecimal precoMedioPosteriorCm2;

    @Column(name = "registrado_em", nullable = false)
    private LocalDateTime registradoEm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estoque_fluido_dtf_id", nullable = false)
    private EstoqueFluidoDtf estoqueFluidoDtf;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getLarguraCm() {
        return larguraCm;
    }

    public void setLarguraCm(BigDecimal larguraCm) {
        this.larguraCm = larguraCm;
    }

    public BigDecimal getComprimentoCm() {
        return comprimentoCm;
    }

    public void setComprimentoCm(BigDecimal comprimentoCm) {
        this.comprimentoCm = comprimentoCm;
    }

    public BigDecimal getAreaLoteCm2() {
        return areaLoteCm2;
    }

    public void setAreaLoteCm2(BigDecimal areaLoteCm2) {
        this.areaLoteCm2 = areaLoteCm2;
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

    public BigDecimal getValorTotalGasto() {
        return valorTotalGasto;
    }

    public void setValorTotalGasto(BigDecimal valorTotalGasto) {
        this.valorTotalGasto = valorTotalGasto;
    }

    public BigDecimal getSaldoAreaAnteriorCm2() {
        return saldoAreaAnteriorCm2;
    }

    public void setSaldoAreaAnteriorCm2(BigDecimal saldoAreaAnteriorCm2) {
        this.saldoAreaAnteriorCm2 = saldoAreaAnteriorCm2;
    }

    public BigDecimal getSaldoAreaPosteriorCm2() {
        return saldoAreaPosteriorCm2;
    }

    public void setSaldoAreaPosteriorCm2(BigDecimal saldoAreaPosteriorCm2) {
        this.saldoAreaPosteriorCm2 = saldoAreaPosteriorCm2;
    }

    public BigDecimal getPrecoMedioAnteriorCm2() {
        return precoMedioAnteriorCm2;
    }

    public void setPrecoMedioAnteriorCm2(BigDecimal precoMedioAnteriorCm2) {
        this.precoMedioAnteriorCm2 = precoMedioAnteriorCm2;
    }

    public BigDecimal getPrecoMedioPosteriorCm2() {
        return precoMedioPosteriorCm2;
    }

    public void setPrecoMedioPosteriorCm2(BigDecimal precoMedioPosteriorCm2) {
        this.precoMedioPosteriorCm2 = precoMedioPosteriorCm2;
    }

    public LocalDateTime getRegistradoEm() {
        return registradoEm;
    }

    public void setRegistradoEm(LocalDateTime registradoEm) {
        this.registradoEm = registradoEm;
    }

    public EstoqueFluidoDtf getEstoqueFluidoDtf() {
        return estoqueFluidoDtf;
    }

    public void setEstoqueFluidoDtf(EstoqueFluidoDtf estoqueFluidoDtf) {
        this.estoqueFluidoDtf = estoqueFluidoDtf;
    }
}