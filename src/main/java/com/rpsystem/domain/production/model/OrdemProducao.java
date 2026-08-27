package com.rpsystem.domain.production.model;

import com.rpsystem.domain.inventory.model.LoteBlankItem;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ordem_producao")
public class OrdemProducao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "descricao_modelo", nullable = false, length = 255)
    private String descricaoModelo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lote_blank_item_id", nullable = false)
    private LoteBlankItem loteBlankItem;

    @Column(name = "quantidade_produzir", nullable = false)
    private Integer quantidadeProduzir;

    @Column(name = "quantidade_disponivel", nullable = false)
    private Integer quantidadeDisponivel = 0;

    @Column(name = "operador_responsavel", nullable = false, length = 120)
    private String operadorResponsavel;

    @Column(name = "fator_aproveitamento_dtf_perc", nullable = false, precision = 5, scale = 2)
    private BigDecimal fatorAproveitamentoDtfPerc;

    @Column(name = "custo_blanks_total", nullable = false, precision = 19, scale = 2)
    private BigDecimal custoBlanksTotal;

    @Column(name = "custo_dtf_total", nullable = false, precision = 19, scale = 2)
    private BigDecimal custoDtfTotal;

    @Column(name = "custo_operacional_total", nullable = false, precision = 19, scale = 2)
    private BigDecimal custoOperacionalTotal;

    @Column(name = "cpv_total", nullable = false, precision = 19, scale = 2)
    private BigDecimal cpvTotal;

    @Column(name = "cpv_unitario_final", nullable = false, precision = 19, scale = 2)
    private BigDecimal cpvUnitarioFinal;

    @Column(name = "foto_mockup_path", length = 500)
    private String fotoMockupPath;

    @Column(name = "registrada_em", nullable = false)
    private LocalDateTime registradaEm;

    @OneToMany(mappedBy = "ordemProducao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrdemProducaoFragmento> fragmentos = new ArrayList<>();

    @OneToMany(mappedBy = "ordemProducao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrdemProducaoConsumoBlank> consumosBlank = new ArrayList<>();

    @OneToMany(mappedBy = "ordemProducao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrdemProducaoConsumoDtf> consumosDtf = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricaoModelo() {
        return descricaoModelo;
    }

    public void setDescricaoModelo(String descricaoModelo) {
        this.descricaoModelo = descricaoModelo;
    }

    public LoteBlankItem getLoteBlankItem() {
        return loteBlankItem;
    }

    public void setLoteBlankItem(LoteBlankItem loteBlankItem) {
        this.loteBlankItem = loteBlankItem;
    }

    public Integer getQuantidadeProduzir() {
        return quantidadeProduzir;
    }

    public void setQuantidadeProduzir(Integer quantidadeProduzir) {
        this.quantidadeProduzir = quantidadeProduzir;
    }

    public Integer getQuantidadeDisponivel() {
        return quantidadeDisponivel;
    }

    public void setQuantidadeDisponivel(Integer quantidadeDisponivel) {
        this.quantidadeDisponivel = quantidadeDisponivel;
    }

    public void abaterQuantidadeDisponivel(int qtd) {
        if (this.quantidadeDisponivel == null || this.quantidadeDisponivel < qtd) {
            throw new IllegalArgumentException("Saldo insuficiente de peças disponíveis para baixa.");
        }
        this.quantidadeDisponivel -= qtd;
    }

    public String getTamanho() {
        return loteBlankItem != null ? loteBlankItem.getTamanho() : "-";
    }

    public String getSkuBase() {
        return loteBlankItem != null ? loteBlankItem.getSkuBase() : "-";
    }

    public String getSkuCompleto() {
        return loteBlankItem != null ? loteBlankItem.getSkuCompleto() : "-";
    }

    public String getOperadorResponsavel() {
        return operadorResponsavel;
    }

    public void setOperadorResponsavel(String operadorResponsavel) {
        this.operadorResponsavel = operadorResponsavel;
    }

    public BigDecimal getFatorAproveitamentoDtfPerc() {
        return fatorAproveitamentoDtfPerc;
    }

    public void setFatorAproveitamentoDtfPerc(BigDecimal fatorAproveitamentoDtfPerc) {
        this.fatorAproveitamentoDtfPerc = fatorAproveitamentoDtfPerc;
    }

    public BigDecimal getCustoBlanksTotal() {
        return custoBlanksTotal;
    }

    public void setCustoBlanksTotal(BigDecimal custoBlanksTotal) {
        this.custoBlanksTotal = custoBlanksTotal;
    }

    public BigDecimal getCustoDtfTotal() {
        return custoDtfTotal;
    }

    public void setCustoDtfTotal(BigDecimal custoDtfTotal) {
        this.custoDtfTotal = custoDtfTotal;
    }

    public BigDecimal getCustoOperacionalTotal() {
        return custoOperacionalTotal;
    }

    public void setCustoOperacionalTotal(BigDecimal custoOperacionalTotal) {
        this.custoOperacionalTotal = custoOperacionalTotal;
    }

    public BigDecimal getCpvTotal() {
        return cpvTotal;
    }

    public void setCpvTotal(BigDecimal cpvTotal) {
        this.cpvTotal = cpvTotal;
    }

    public BigDecimal getCpvUnitarioFinal() {
        return cpvUnitarioFinal;
    }

    public void setCpvUnitarioFinal(BigDecimal cpvUnitarioFinal) {
        this.cpvUnitarioFinal = cpvUnitarioFinal;
    }

    public String getFotoMockupPath() {
        return fotoMockupPath;
    }

    public void setFotoMockupPath(String fotoMockupPath) {
        this.fotoMockupPath = fotoMockupPath;
    }

    public LocalDateTime getRegistradaEm() {
        return registradaEm;
    }

    public void setRegistradaEm(LocalDateTime registradaEm) {
        this.registradaEm = registradaEm;
    }

    public List<OrdemProducaoFragmento> getFragmentos() {
        return fragmentos;
    }

    public void setFragmentos(List<OrdemProducaoFragmento> fragmentos) {
        this.fragmentos = fragmentos;
    }

    public List<OrdemProducaoConsumoBlank> getConsumosBlank() {
        return consumosBlank;
    }

    public void setConsumosBlank(List<OrdemProducaoConsumoBlank> consumosBlank) {
        this.consumosBlank = consumosBlank;
    }

    public List<OrdemProducaoConsumoDtf> getConsumosDtf() {
        return consumosDtf;
    }

    public void setConsumosDtf(List<OrdemProducaoConsumoDtf> consumosDtf) {
        this.consumosDtf = consumosDtf;
    }

    public void adicionarFragmento(OrdemProducaoFragmento fragmento) {
        fragmento.setOrdemProducao(this);
        this.fragmentos.add(fragmento);
    }

    public void adicionarConsumoBlank(OrdemProducaoConsumoBlank consumoBlank) {
        consumoBlank.setOrdemProducao(this);
        this.consumosBlank.add(consumoBlank);
    }

    public void adicionarConsumoDtf(OrdemProducaoConsumoDtf consumoDtf) {
        consumoDtf.setOrdemProducao(this);
        this.consumosDtf.add(consumoDtf);
    }
}