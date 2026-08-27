package com.rpsystem.domain.sales.model;

import com.rpsystem.domain.finance.model.ContaFinanceira;
import com.rpsystem.domain.production.model.OrdemProducao;
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
@Table(name = "venda")
public class Venda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_venda", nullable = false, length = 60, unique = true)
    private String codigoVenda;

    @Enumerated(EnumType.STRING)
    @Column(name = "canal_venda", nullable = false, length = 50)
    private CanalVenda canalVenda;

    @Enumerated(EnumType.STRING)
    @Column(name = "forma_pagamento", nullable = false, length = 50)
    private FormaPagamento formaPagamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ordem_producao_id")
    private OrdemProducao ordemProducao;

    @Column(name = "descricao_produto", nullable = false, length = 255)
    private String descricaoProduto;

    @Column(name = "sku_base", length = 120)
    private String skuBase;

    @Column(name = "tamanho", length = 20)
    private String tamanho;

    @Column(name = "quantidade", nullable = false)
    private Integer quantidade;

    @Column(name = "preco_venda_unitario", nullable = false, precision = 19, scale = 2)
    private BigDecimal precoVendaUnitario;

    @Column(name = "valor_total", nullable = false, precision = 19, scale = 2)
    private BigDecimal valorTotal;

    @Column(name = "cpv_unitario", nullable = false, precision = 19, scale = 2)
    private BigDecimal cpvUnitario;

    @Column(name = "cpv_total", nullable = false, precision = 19, scale = 2)
    private BigDecimal cpvTotal;

    @Column(name = "lucro_bruto", nullable = false, precision = 19, scale = 2)
    private BigDecimal lucroBruto;

    @Column(name = "margem_percentual", precision = 7, scale = 2)
    private BigDecimal margemPercentual;

    @Column(name = "data_venda", nullable = false)
    private LocalDateTime dataVenda;

    @Column(name = "nome_cliente", length = 150)
    private String nomeCliente;

    @Column(name = "observacoes", length = 500)
    private String observacoes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conta_financeira_id", nullable = false)
    private ContaFinanceira contaFinanceira;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigoVenda() {
        return codigoVenda;
    }

    public void setCodigoVenda(String codigoVenda) {
        this.codigoVenda = codigoVenda;
    }

    public CanalVenda getCanalVenda() {
        return canalVenda;
    }

    public void setCanalVenda(CanalVenda canalVenda) {
        this.canalVenda = canalVenda;
    }

    public FormaPagamento getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(FormaPagamento formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public OrdemProducao getOrdemProducao() {
        return ordemProducao;
    }

    public void setOrdemProducao(OrdemProducao ordemProducao) {
        this.ordemProducao = ordemProducao;
    }

    public String getDescricaoProduto() {
        return descricaoProduto;
    }

    public void setDescricaoProduto(String descricaoProduto) {
        this.descricaoProduto = descricaoProduto;
    }

    public String getSkuBase() {
        return skuBase;
    }

    public void setSkuBase(String skuBase) {
        this.skuBase = skuBase;
    }

    public String getTamanho() {
        return tamanho;
    }

    public void setTamanho(String tamanho) {
        this.tamanho = tamanho;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getPrecoVendaUnitario() {
        return precoVendaUnitario;
    }

    public void setPrecoVendaUnitario(BigDecimal precoVendaUnitario) {
        this.precoVendaUnitario = precoVendaUnitario;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public BigDecimal getCpvUnitario() {
        return cpvUnitario;
    }

    public void setCpvUnitario(BigDecimal cpvUnitario) {
        this.cpvUnitario = cpvUnitario;
    }

    public BigDecimal getCpvTotal() {
        return cpvTotal;
    }

    public void setCpvTotal(BigDecimal cpvTotal) {
        this.cpvTotal = cpvTotal;
    }

    public BigDecimal getLucroBruto() {
        return lucroBruto;
    }

    public void setLucroBruto(BigDecimal lucroBruto) {
        this.lucroBruto = lucroBruto;
    }

    public BigDecimal getMargemPercentual() {
        return margemPercentual;
    }

    public void setMargemPercentual(BigDecimal margemPercentual) {
        this.margemPercentual = margemPercentual;
    }

    public LocalDateTime getDataVenda() {
        return dataVenda;
    }

    public void setDataVenda(LocalDateTime dataVenda) {
        this.dataVenda = dataVenda;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public ContaFinanceira getContaFinanceira() {
        return contaFinanceira;
    }

    public void setContaFinanceira(ContaFinanceira contaFinanceira) {
        this.contaFinanceira = contaFinanceira;
    }
}
