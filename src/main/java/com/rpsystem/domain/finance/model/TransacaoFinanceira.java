package com.rpsystem.domain.finance.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "transacao_financeira")
public class TransacaoFinanceira {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_transacao", nullable = false, unique = true, length = 50)
    private String codigoTransacao;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimento", nullable = false, length = 10)
    private TipoMovimento tipoMovimento;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_evento", nullable = false, length = 50)
    private TipoEventoFinanceiro tipoEvento;

    @Column(name = "descricao", nullable = false, length = 255)
    private String descricao;

    @Column(name = "valor", nullable = false, precision = 19, scale = 2)
    private BigDecimal valor;

    @Column(name = "data_competencia", nullable = false)
    private LocalDateTime dataCompetencia;

    @Column(name = "data_liquidacao")
    private LocalDateTime dataLiquidacao;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private StatusTransacao status;

    @Enumerated(EnumType.STRING)
    @Column(name = "modulo_origem", nullable = false, length = 50)
    private ModuloOrigem moduloOrigem;

    @Column(name = "origem_id")
    private Long origemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conta_financeira_id", nullable = false)
    private ContaFinanceira contaFinanceira;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private CategoriaFinanceira categoriaFinanceira;

    @Column(name = "cpv_historico", precision = 19, scale = 2)
    private BigDecimal cpvHistorico;

    @Column(name = "margem_nominal", precision = 19, scale = 2)
    private BigDecimal margemNominal;

    @Column(name = "observacoes", length = 500)
    private String observacoes;

    public TransacaoFinanceira() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigoTransacao() {
        return codigoTransacao;
    }

    public void setCodigoTransacao(String codigoTransacao) {
        this.codigoTransacao = codigoTransacao;
    }

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

    public LocalDateTime getDataCompetencia() {
        return dataCompetencia;
    }

    public void setDataCompetencia(LocalDateTime dataCompetencia) {
        this.dataCompetencia = dataCompetencia;
    }

    public LocalDateTime getDataLiquidacao() {
        return dataLiquidacao;
    }

    public void setDataLiquidacao(LocalDateTime dataLiquidacao) {
        this.dataLiquidacao = dataLiquidacao;
    }

    public StatusTransacao getStatus() {
        return status;
    }

    public void setStatus(StatusTransacao status) {
        this.status = status;
    }

    public ModuloOrigem getModuloOrigem() {
        return moduloOrigem;
    }

    public void setModuloOrigem(ModuloOrigem moduloOrigem) {
        this.moduloOrigem = moduloOrigem;
    }

    public Long getOrigemId() {
        return origemId;
    }

    public void setOrigemId(Long origemId) {
        this.origemId = origemId;
    }

    public ContaFinanceira getContaFinanceira() {
        return contaFinanceira;
    }

    public void setContaFinanceira(ContaFinanceira contaFinanceira) {
        this.contaFinanceira = contaFinanceira;
    }

    public CategoriaFinanceira getCategoriaFinanceira() {
        return categoriaFinanceira;
    }

    public void setCategoriaFinanceira(CategoriaFinanceira categoriaFinanceira) {
        this.categoriaFinanceira = categoriaFinanceira;
    }

    public BigDecimal getCpvHistorico() {
        return cpvHistorico;
    }

    public void setCpvHistorico(BigDecimal cpvHistorico) {
        this.cpvHistorico = cpvHistorico;
    }

    public BigDecimal getMargemNominal() {
        return margemNominal;
    }

    public void setMargemNominal(BigDecimal margemNominal) {
        this.margemNominal = margemNominal;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}
