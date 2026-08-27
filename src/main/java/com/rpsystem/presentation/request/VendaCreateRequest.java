package com.rpsystem.presentation.request;

import com.rpsystem.domain.sales.model.CanalVenda;
import com.rpsystem.domain.sales.model.FormaPagamento;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class VendaCreateRequest {

    @NotNull(message = "Selecione o produto/peça para a venda.")
    private Long ordemProducaoId;

    @NotNull(message = "Informe a quantidade.")
    @DecimalMin(value = "1", message = "A quantidade vendida deve ser no mínimo 1.")
    private Integer quantidade = 1;

    @NotNull(message = "Informe o preço unitário de venda.")
    @DecimalMin(value = "0.01", message = "O preço de venda deve ser maior que zero.")
    private BigDecimal precoVendaUnitario;

    private CanalVenda canalVenda = CanalVenda.DIRETA_BOCA_A_BOCA;

    private FormaPagamento formaPagamento = FormaPagamento.PIX;

    private String nomeCliente;

    private Long contaId;

    private String observacoes;

    public Long getOrdemProducaoId() {
        return ordemProducaoId;
    }

    public void setOrdemProducaoId(Long ordemProducaoId) {
        this.ordemProducaoId = ordemProducaoId;
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

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public Long getContaId() {
        return contaId;
    }

    public void setContaId(Long contaId) {
        this.contaId = contaId;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}
