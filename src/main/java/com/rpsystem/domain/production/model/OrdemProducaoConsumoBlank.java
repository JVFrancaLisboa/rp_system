package com.rpsystem.domain.production.model;

import com.rpsystem.domain.inventory.model.LoteBlankItem;
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
@Table(name = "ordem_producao_consumo_blank")
public class OrdemProducaoConsumoBlank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ordem_producao_id", nullable = false)
    private OrdemProducao ordemProducao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lote_blank_item_id", nullable = false)
    private LoteBlankItem loteBlankItem;

    @Column(name = "quantidade_consumida", nullable = false)
    private Integer quantidadeConsumida;

    @Column(name = "custo_unitario_aplicado", nullable = false, precision = 19, scale = 2)
    private BigDecimal custoUnitarioAplicado;

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

    public LoteBlankItem getLoteBlankItem() {
        return loteBlankItem;
    }

    public void setLoteBlankItem(LoteBlankItem loteBlankItem) {
        this.loteBlankItem = loteBlankItem;
    }

    public Integer getQuantidadeConsumida() {
        return quantidadeConsumida;
    }

    public void setQuantidadeConsumida(Integer quantidadeConsumida) {
        this.quantidadeConsumida = quantidadeConsumida;
    }

    public BigDecimal getCustoUnitarioAplicado() {
        return custoUnitarioAplicado;
    }

    public void setCustoUnitarioAplicado(BigDecimal custoUnitarioAplicado) {
        this.custoUnitarioAplicado = custoUnitarioAplicado;
    }

    public BigDecimal getCustoTotal() {
        return custoTotal;
    }

    public void setCustoTotal(BigDecimal custoTotal) {
        this.custoTotal = custoTotal;
    }
}