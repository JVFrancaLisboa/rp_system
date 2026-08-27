package com.rpsystem.domain.inventory.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lote_blank")
public class LoteBlank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "data_entrada", nullable = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime dataEntrada;

    @Column(name = "valor_frete_total", nullable = false, precision = 19, scale = 2)
    private BigDecimal valorFreteTotal;

    @OneToMany(mappedBy = "loteBlank", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LoteBlankItem> itens = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDataEntrada() {
        return dataEntrada;
    }

    public void setDataEntrada(LocalDateTime dataEntrada) {
        this.dataEntrada = dataEntrada;
    }

    public BigDecimal getValorFreteTotal() {
        return valorFreteTotal;
    }

    public void setValorFreteTotal(BigDecimal valorFreteTotal) {
        this.valorFreteTotal = valorFreteTotal;
    }

    public List<LoteBlankItem> getItens() {
        return itens;
    }

    public void setItens(List<LoteBlankItem> itens) {
        this.itens = itens;
    }

    public void adicionarItem(LoteBlankItem item) {
        item.setLoteBlank(this);
        this.itens.add(item);
    }
}