package com.rpsystem.domain.finance.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "estoque_fluido_dtf")
public class EstoqueFluidoDtf {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "area_total_cm2", nullable = false, precision = 19, scale = 2)
    private BigDecimal areaTotalCm2;

    @Column(name = "custo_total_acumulado", nullable = false, precision = 19, scale = 2)
    private BigDecimal custoTotalAcumulado;

    @Column(name = "preco_medio_por_cm2", nullable = false, precision = 19, scale = 6)
    private BigDecimal precoMedioPorCm2;

    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAreaTotalCm2() {
        return areaTotalCm2;
    }

    public void setAreaTotalCm2(BigDecimal areaTotalCm2) {
        this.areaTotalCm2 = areaTotalCm2;
    }

    public BigDecimal getCustoTotalAcumulado() {
        return custoTotalAcumulado;
    }

    public void setCustoTotalAcumulado(BigDecimal custoTotalAcumulado) {
        this.custoTotalAcumulado = custoTotalAcumulado;
    }

    public BigDecimal getPrecoMedioPorCm2() {
        return precoMedioPorCm2;
    }

    public void setPrecoMedioPorCm2(BigDecimal precoMedioPorCm2) {
        this.precoMedioPorCm2 = precoMedioPorCm2;
    }

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    public void setAtualizadoEm(LocalDateTime atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
    }
}