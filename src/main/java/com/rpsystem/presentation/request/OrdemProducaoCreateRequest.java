package com.rpsystem.presentation.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class OrdemProducaoCreateRequest {

    @NotBlank
    private String descricaoModelo;

    @NotNull
    private Long skuBlankId;

    @NotNull
    @DecimalMin(value = "1")
    private Integer quantidadeProduzir;

    @NotBlank
    private String operadorResponsavel;

    @NotNull
    @DecimalMin(value = "1")
    @DecimalMax(value = "100")
    private BigDecimal fatorAproveitamentoPerc;

    private MultipartFile fotoMockup;

    @Valid
    private List<FragmentoRequest> fragmentos = new ArrayList<>();

    public String getDescricaoModelo() {
        return descricaoModelo;
    }

    public void setDescricaoModelo(String descricaoModelo) {
        this.descricaoModelo = descricaoModelo;
    }

    public Long getSkuBlankId() {
        return skuBlankId;
    }

    public void setSkuBlankId(Long skuBlankId) {
        this.skuBlankId = skuBlankId;
    }

    public Integer getQuantidadeProduzir() {
        return quantidadeProduzir;
    }

    public void setQuantidadeProduzir(Integer quantidadeProduzir) {
        this.quantidadeProduzir = quantidadeProduzir;
    }

    public String getOperadorResponsavel() {
        return operadorResponsavel;
    }

    public void setOperadorResponsavel(String operadorResponsavel) {
        this.operadorResponsavel = operadorResponsavel;
    }

    public BigDecimal getFatorAproveitamentoPerc() {
        return fatorAproveitamentoPerc;
    }

    public void setFatorAproveitamentoPerc(BigDecimal fatorAproveitamentoPerc) {
        this.fatorAproveitamentoPerc = fatorAproveitamentoPerc;
    }

    public MultipartFile getFotoMockup() {
        return fotoMockup;
    }

    public void setFotoMockup(MultipartFile fotoMockup) {
        this.fotoMockup = fotoMockup;
    }

    public List<FragmentoRequest> getFragmentos() {
        return fragmentos;
    }

    public void setFragmentos(List<FragmentoRequest> fragmentos) {
        this.fragmentos = fragmentos;
    }

    public static class FragmentoRequest {

        @NotBlank
        private String descricao;

        @NotNull
        @DecimalMin(value = "0.01")
        private BigDecimal largura;

        @NotNull
        @DecimalMin(value = "0.01")
        private BigDecimal altura;

        public String getDescricao() {
            return descricao;
        }

        public void setDescricao(String descricao) {
            this.descricao = descricao;
        }

        public BigDecimal getLargura() {
            return largura;
        }

        public void setLargura(BigDecimal largura) {
            this.largura = largura;
        }

        public BigDecimal getAltura() {
            return altura;
        }

        public void setAltura(BigDecimal altura) {
            this.altura = altura;
        }
    }
}