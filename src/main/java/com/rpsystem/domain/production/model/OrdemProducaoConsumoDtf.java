package com.rpsystem.domain.production.model;

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

@Entity
@Table(name = "ordem_producao_consumo_dtf")
public class OrdemProducaoConsumoDtf {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ordem_producao_id", nullable = false)
    private OrdemProducao ordemProducao;

    @Column(name = "area_consumida_cm2", nullable = false, precision = 19, scale = 2)
    private BigDecimal areaConsumidaCm2;

    @Column(name = "custo_medio_aplicado", nullable = false, precision = 19, scale = 6)
    private BigDecimal custoMedioAplicado;

    @Column(name = "custo_total", nullable = false, precision = 19, scale = 2)
    private BigDecimal custoTotal;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OrdemProducao getOrdemProducao() {
        return ordemProducao;
    }

    public void setOrdemProducao(OrdemProducao ordemProducao) {
        this.ordemProducao = ordemProducao;
    }

    public BigDecimal getAreaConsumidaCm2() {
        return areaConsumidaCm2;
    }

    public void setAreaConsumidaCm2(BigDecimal areaConsumidaCm2) {
        this.areaConsumidaCm2 = areaConsumidaCm2;
    }

    public BigDecimal getCustoMedioAplicado() {
        return custoMedioAplicado;
    }

    public void setCustoMedioAplicado(BigDecimal custoMedioAplicado) {
        this.custoMedioAplicado = custoMedioAplicado;
    }

    public BigDecimal getCustoTotal() {
        return custoTotal;
    }

    public void setCustoTotal(BigDecimal custoTotal) {
        this.custoTotal = custoTotal;
    }
}