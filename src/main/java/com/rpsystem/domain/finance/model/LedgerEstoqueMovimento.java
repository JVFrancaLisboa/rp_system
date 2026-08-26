package com.rpsystem.domain.finance.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ledger_estoque_movimento")
public class LedgerEstoqueMovimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_evento", nullable = false, length = 50)
    private String codigoEvento;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_operacao", nullable = false, length = 50)
    private TipoOperacaoEstoque tipoOperacao;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_insumo", nullable = false, length = 30)
    private TipoInsumo tipoInsumo;

    @Column(name = "variacao_volumetrica", nullable = false, length = 50)
    private String variacaoVolumetrica;

    @Column(name = "quantidade_delta", nullable = false, precision = 19, scale = 2)
    private BigDecimal quantidadeDelta;

    @Column(name = "impacto_detalhado", nullable = false, length = 255)
    private String impactoDetalhado;

    @Column(name = "referencia_matriz", nullable = false, length = 255)
    private String referenciaMatriz;

    @Column(name = "custo_unitario_aplicado", nullable = false, precision = 19, scale = 6)
    private BigDecimal custoUnitarioAplicado;

    @Column(name = "custo_total_impactado", nullable = false, precision = 19, scale = 2)
    private BigDecimal custoTotalImpactado;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    public LedgerEstoqueMovimento() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigoEvento() {
        return codigoEvento;
    }

    public void setCodigoEvento(String codigoEvento) {
        this.codigoEvento = codigoEvento;
    }

    public TipoOperacaoEstoque getTipoOperacao() {
        return tipoOperacao;
    }

    public void setTipoOperacao(TipoOperacaoEstoque tipoOperacao) {
        this.tipoOperacao = tipoOperacao;
    }

    public TipoInsumo getTipoInsumo() {
        return tipoInsumo;
    }

    public void setTipoInsumo(TipoInsumo tipoInsumo) {
        this.tipoInsumo = tipoInsumo;
    }

    public String getVariacaoVolumetrica() {
        return variacaoVolumetrica;
    }

    public void setVariacaoVolumetrica(String variacaoVolumetrica) {
        this.variacaoVolumetrica = variacaoVolumetrica;
    }

    public BigDecimal getQuantidadeDelta() {
        return quantidadeDelta;
    }

    public void setQuantidadeDelta(BigDecimal quantidadeDelta) {
        this.quantidadeDelta = quantidadeDelta;
    }

    public String getImpactoDetalhado() {
        return impactoDetalhado;
    }

    public void setImpactoDetalhado(String impactoDetalhado) {
        this.impactoDetalhado = impactoDetalhado;
    }

    public String getReferenciaMatriz() {
        return referenciaMatriz;
    }

    public void setReferenciaMatriz(String referenciaMatriz) {
        this.referenciaMatriz = referenciaMatriz;
    }

    public BigDecimal getCustoUnitarioAplicado() {
        return custoUnitarioAplicado;
    }

    public void setCustoUnitarioAplicado(BigDecimal custoUnitarioAplicado) {
        this.custoUnitarioAplicado = custoUnitarioAplicado;
    }

    public BigDecimal getCustoTotalImpactado() {
        return custoTotalImpactado;
    }

    public void setCustoTotalImpactado(BigDecimal custoTotalImpactado) {
        this.custoTotalImpactado = custoTotalImpactado;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }
}
