package com.rpsystem.domain.inventory.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import java.math.BigDecimal;

@Entity
@Table(name = "lote_blank_item")
public class LoteBlankItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sku_base", nullable = false, length = 120)
    private String skuBase;

    @Column(nullable = false, length = 10)
    private String tamanho;

    @Column(name = "quantidade_adquirida", nullable = false)
    private Integer quantidadeAdquirida;

    @Column(name = "quantidade_disponivel", nullable = false)
    private Integer quantidadeDisponivel;

    @Column(name = "preco_nota_unitario", nullable = false, precision = 19, scale = 2)
    private BigDecimal precoNotaUnitario;

    @Column(name = "custo_com_frete_calculado", nullable = false, precision = 19, scale = 2)
    private BigDecimal custoComFreteCalculado;

    @Column(name = "foto_path", length = 500)
    private String fotoPath;

    @ManyToOne
    @JoinColumn(name = "lote_blank_id", nullable = false)
    private LoteBlank loteBlank;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Integer getQuantidadeAdquirida() {
        return quantidadeAdquirida;
    }

    public void setQuantidadeAdquirida(Integer quantidadeAdquirida) {
        this.quantidadeAdquirida = quantidadeAdquirida;
    }

    public Integer getQuantidadeDisponivel() {
        return quantidadeDisponivel;
    }

    public void setQuantidadeDisponivel(Integer quantidadeDisponivel) {
        this.quantidadeDisponivel = quantidadeDisponivel;
    }

    public BigDecimal getPrecoNotaUnitario() {
        return precoNotaUnitario;
    }

    public void setPrecoNotaUnitario(BigDecimal precoNotaUnitario) {
        this.precoNotaUnitario = precoNotaUnitario;
    }

    public BigDecimal getCustoComFreteCalculado() {
        return custoComFreteCalculado;
    }

    public void setCustoComFreteCalculado(BigDecimal custoComFreteCalculado) {
        this.custoComFreteCalculado = custoComFreteCalculado;
    }

    public String getFotoPath() {
        return fotoPath;
    }

    public void setFotoPath(String fotoPath) {
        this.fotoPath = fotoPath;
    }

    public LoteBlank getLoteBlank() {
        return loteBlank;
    }

    public void setLoteBlank(LoteBlank loteBlank) {
        this.loteBlank = loteBlank;
    }

    @Transient
    public String getSkuCompleto() {
        return (skuBase == null ? "" : skuBase) + (tamanho == null || tamanho.isBlank() ? "" : "-" + tamanho);
    }
}