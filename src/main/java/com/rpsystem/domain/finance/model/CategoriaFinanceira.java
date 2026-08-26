package com.rpsystem.domain.finance.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "categoria_financeira")
public class CategoriaFinanceira {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 30)
    private TipoCategoriaFinanceira tipo;

    @Column(name = "cor_hex", length = 20)
    private String corHex;

    public CategoriaFinanceira() {
    }

    public CategoriaFinanceira(String nome, TipoCategoriaFinanceira tipo, String corHex) {
        this.nome = nome;
        this.tipo = tipo;
        this.corHex = corHex;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public TipoCategoriaFinanceira getTipo() {
        return tipo;
    }

    public void setTipo(TipoCategoriaFinanceira tipo) {
        this.tipo = tipo;
    }

    public String getCorHex() {
        return corHex;
    }

    public void setCorHex(String corHex) {
        this.corHex = corHex;
    }
}
