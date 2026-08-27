package com.rpsystem.application.sales;

import com.rpsystem.application.finance.FluxoCaixaService;
import com.rpsystem.domain.finance.model.ContaFinanceira;
import com.rpsystem.domain.finance.model.TipoConta;
import com.rpsystem.domain.finance.repository.ContaFinanceiraRepository;
import com.rpsystem.domain.production.model.OrdemProducao;
import com.rpsystem.domain.production.repository.OrdemProducaoRepository;
import com.rpsystem.domain.sales.model.CanalVenda;
import com.rpsystem.domain.sales.model.FormaPagamento;
import com.rpsystem.domain.sales.model.Venda;
import com.rpsystem.domain.sales.repository.VendaRepository;
import com.rpsystem.presentation.request.VendaCreateRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class VendaService {

    private final VendaRepository vendaRepository;
    private final OrdemProducaoRepository ordemProducaoRepository;
    private final ContaFinanceiraRepository contaFinanceiraRepository;
    private final FluxoCaixaService fluxoCaixaService;

    public VendaService(
            VendaRepository vendaRepository,
            OrdemProducaoRepository ordemProducaoRepository,
            ContaFinanceiraRepository contaFinanceiraRepository,
            FluxoCaixaService fluxoCaixaService
    ) {
        this.vendaRepository = vendaRepository;
        this.ordemProducaoRepository = ordemProducaoRepository;
        this.contaFinanceiraRepository = contaFinanceiraRepository;
        this.fluxoCaixaService = fluxoCaixaService;
    }

    @Transactional
    public Venda registrarVenda(VendaCreateRequest request) {
        if (request == null || request.getOrdemProducaoId() == null) {
            throw new IllegalArgumentException("Peça ou ordem de produção inválida.");
        }

        int quantidade = request.getQuantidade() != null && request.getQuantidade() > 0 ? request.getQuantidade() : 1;

        OrdemProducao ordem = ordemProducaoRepository.findById(request.getOrdemProducaoId())
                .orElseThrow(() -> new IllegalArgumentException("Peça / Ordem de Produção não encontrada."));

        if (ordem.getQuantidadeDisponivel() == null || ordem.getQuantidadeDisponivel() < quantidade) {
            int disponivel = ordem.getQuantidadeDisponivel() != null ? ordem.getQuantidadeDisponivel() : 0;
            throw new IllegalArgumentException("Saldo insuficiente em estoque. Disponível: " + disponivel + " peça(s).");
        }

        // Abate o saldo de estoque da peça acabada
        ordem.abaterQuantidadeDisponivel(quantidade);
        ordemProducaoRepository.save(ordem);

        BigDecimal precoUnitario = request.getPrecoVendaUnitario().setScale(2, RoundingMode.HALF_UP);
        BigDecimal valorTotal = precoUnitario.multiply(BigDecimal.valueOf(quantidade)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal cpvUnitario = ordem.getCpvUnitarioFinal() != null ? ordem.getCpvUnitarioFinal() : BigDecimal.ZERO;
        BigDecimal cpvTotal = cpvUnitario.multiply(BigDecimal.valueOf(quantidade)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal lucroBruto = valorTotal.subtract(cpvTotal).setScale(2, RoundingMode.HALF_UP);

        BigDecimal margemPercentual = BigDecimal.ZERO;
        if (valorTotal.compareTo(BigDecimal.ZERO) > 0) {
            margemPercentual = lucroBruto.multiply(BigDecimal.valueOf(100)).divide(valorTotal, 2, RoundingMode.HALF_UP);
        }

        ContaFinanceira conta = request.getContaId() != null
                ? contaFinanceiraRepository.findById(request.getContaId()).orElseGet(this::obterContaPadrao)
                : obterContaPadrao();

        Venda venda = new Venda();
        venda.setCodigoVenda(gerarCodigoVenda());
        venda.setCanalVenda(request.getCanalVenda() != null ? request.getCanalVenda() : CanalVenda.DIRETA_BOCA_A_BOCA);
        venda.setFormaPagamento(request.getFormaPagamento() != null ? request.getFormaPagamento() : FormaPagamento.PIX);
        venda.setOrdemProducao(ordem);
        venda.setDescricaoProduto(ordem.getDescricaoModelo());
        venda.setSkuBase(ordem.getSkuBase());
        venda.setTamanho(ordem.getTamanho());
        venda.setQuantidade(quantidade);
        venda.setPrecoVendaUnitario(precoUnitario);
        venda.setValorTotal(valorTotal);
        venda.setCpvUnitario(cpvUnitario);
        venda.setCpvTotal(cpvTotal);
        venda.setLucroBruto(lucroBruto);
        venda.setMargemPercentual(margemPercentual);
        venda.setDataVenda(LocalDateTime.now());
        venda.setNomeCliente(request.getNomeCliente() != null && !request.getNomeCliente().isBlank() ? request.getNomeCliente().trim() : null);
        venda.setObservacoes(request.getObservacoes());
        venda.setContaFinanceira(conta);

        Venda vendaSalva = vendaRepository.save(venda);

        // Integração contábil automática com o Fluxo de Caixa e Ledger Físico
        fluxoCaixaService.registrarVenda(vendaSalva);

        return vendaSalva;
    }

    public List<OrdemProducao> listarPecasDisponiveis() {
        return ordemProducaoRepository.findByQuantidadeDisponivelGreaterThanOrderByRegistradaEmDesc(0);
    }

    public List<Venda> listarVendasRecentes() {
        return vendaRepository.findTop50ByOrderByDataVendaDescIdDesc();
    }

    public List<ContaFinanceira> listarContas() {
        return contaFinanceiraRepository.findByAtivoTrue();
    }

    public KpisVendas obterKpisVendas() {
        BigDecimal totalFaturamento = vendaRepository.somarFaturamentoTotal();
        BigDecimal totalLucroBruto = vendaRepository.somarLucroBrutoTotal();
        long totalPecasVendidas = vendaRepository.somarTotalPecasVendidas();
        long totalPecasEstoque = ordemProducaoRepository.somarQuantidadeDisponivel();

        BigDecimal margemMedia = BigDecimal.ZERO;
        if (totalFaturamento != null && totalFaturamento.compareTo(BigDecimal.ZERO) > 0 && totalLucroBruto != null) {
            margemMedia = totalLucroBruto.multiply(BigDecimal.valueOf(100)).divide(totalFaturamento, 1, RoundingMode.HALF_UP);
        }

        return new KpisVendas(
                totalFaturamento != null ? totalFaturamento : BigDecimal.ZERO,
                totalLucroBruto != null ? totalLucroBruto : BigDecimal.ZERO,
                totalPecasVendidas,
                totalPecasEstoque,
                margemMedia
        );
    }

    private ContaFinanceira obterContaPadrao() {
        return contaFinanceiraRepository.findFirstByAtivoTrueOrderByIdAsc().orElseGet(() -> {
            ContaFinanceira nova = new ContaFinanceira("Caixa Operacional Principal", TipoConta.CAIXA_INTERNO, BigDecimal.ZERO);
            return contaFinanceiraRepository.save(nova);
        });
    }

    private String gerarCodigoVenda() {
        return "VND-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    public record KpisVendas(
            BigDecimal totalFaturamento,
            BigDecimal totalLucroBruto,
            long totalPecasVendidas,
            long totalPecasEstoque,
            BigDecimal margemMediaPercentual
    ) {}
}
