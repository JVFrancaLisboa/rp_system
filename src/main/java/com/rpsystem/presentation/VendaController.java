package com.rpsystem.presentation;

import com.rpsystem.application.sales.VendaService;
import com.rpsystem.domain.production.model.OrdemProducao;
import com.rpsystem.domain.sales.model.CanalVenda;
import com.rpsystem.domain.sales.model.FormaPagamento;
import com.rpsystem.domain.sales.model.Venda;
import com.rpsystem.presentation.request.VendaCreateRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Controller
public class VendaController {

    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");
    private static final DateTimeFormatter DATA_HORA_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final VendaService vendaService;

    public VendaController(VendaService vendaService) {
        this.vendaService = vendaService;
    }

    @GetMapping("/vendas")
    public String viewVendas(Model model) {
        VendaService.KpisVendas kpis = vendaService.obterKpisVendas();
        List<OrdemProducao> pecasDisponiveis = vendaService.listarPecasDisponiveis();
        List<Venda> vendasRecentes = vendaService.listarVendasRecentes();

        NumberFormat moedaFormat = NumberFormat.getCurrencyInstance(PT_BR);

        List<PecaEstoqueCardView> pecasCards = pecasDisponiveis.stream()
                .map(ordem -> {
                    BigDecimal cpv = ordem.getCpvUnitarioFinal() != null ? ordem.getCpvUnitarioFinal() : BigDecimal.ZERO;
                    BigDecimal precoSugerido25 = cpv.multiply(BigDecimal.valueOf(2.5));
                    BigDecimal precoSugerido35 = cpv.multiply(BigDecimal.valueOf(3.5));

                    return new PecaEstoqueCardView(
                            ordem.getId(),
                            ordem.getDescricaoModelo(),
                            ordem.getSkuBase(),
                            ordem.getTamanho(),
                            ordem.getFotoMockupPath() != null ? ordem.getFotoMockupPath() : "/images/logo.png",
                            ordem.getQuantidadeDisponivel(),
                            ordem.getQuantidadeProduzir(),
                            moedaFormat.format(cpv),
                            cpv,
                            moedaFormat.format(precoSugerido25),
                            precoSugerido25,
                            moedaFormat.format(precoSugerido35),
                            precoSugerido35
                    );
                })
                .toList();

        List<VendaLinhaView> vendasLinhas = vendasRecentes.stream()
                .map(venda -> {
                    String timestamp = venda.getDataVenda() != null
                            ? DATA_HORA_FORMATTER.format(venda.getDataVenda())
                            : "--/--/---- --:--";

                    return new VendaLinhaView(
                            timestamp,
                            venda.getCodigoVenda(),
                            venda.getCanalVenda() != null ? venda.getCanalVenda().getDescricao() : "Direta",
                            venda.getCanalVenda() != null ? venda.getCanalVenda().name() : "DIRETA_BOCA_A_BOCA",
                            venda.getFormaPagamento() != null ? venda.getFormaPagamento().getDescricao() : "PIX",
                            venda.getDescricaoProduto(),
                            venda.getTamanho() != null ? venda.getTamanho() : "-",
                            venda.getQuantidade(),
                            moedaFormat.format(venda.getPrecoVendaUnitario() != null ? venda.getPrecoVendaUnitario() : BigDecimal.ZERO),
                            moedaFormat.format(venda.getValorTotal() != null ? venda.getValorTotal() : BigDecimal.ZERO),
                            moedaFormat.format(venda.getCpvTotal() != null ? venda.getCpvTotal() : BigDecimal.ZERO),
                            moedaFormat.format(venda.getLucroBruto() != null ? venda.getLucroBruto() : BigDecimal.ZERO),
                            (venda.getMargemPercentual() != null ? venda.getMargemPercentual().toPlainString() : "0") + "%",
                            venda.getNomeCliente() != null ? venda.getNomeCliente() : "Balcão / Anônimo"
                    );
                })
                .toList();

        model.addAttribute("kpis", kpis);
        model.addAttribute("totalFaturamentoFormatado", moedaFormat.format(kpis.totalFaturamento()));
        model.addAttribute("totalLucroBrutoFormatado", moedaFormat.format(kpis.totalLucroBruto()));
        model.addAttribute("totalPecasVendidas", kpis.totalPecasVendidas());
        model.addAttribute("totalPecasEstoque", kpis.totalPecasEstoque());
        model.addAttribute("margemMediaFormatada", kpis.margemMediaPercentual() + "%");

        model.addAttribute("pecasDisponiveis", pecasCards);
        model.addAttribute("temPecasDisponiveis", !pecasCards.isEmpty());
        model.addAttribute("vendas", vendasLinhas);
        model.addAttribute("contas", vendaService.listarContas());
        model.addAttribute("canaisVenda", CanalVenda.values());
        model.addAttribute("formasPagamento", FormaPagamento.values());

        return "vendas";
    }

    @PostMapping("/api/v1/vendas")
    public String registrarVenda(@ModelAttribute VendaCreateRequest request, RedirectAttributes redirectAttributes) {
        try {
            vendaService.registrarVenda(request);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Venda registrada e integrada ao fluxo de caixa com sucesso!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("mensagemErro", ex.getMessage());
        }
        return "redirect:/vendas";
    }

    public record PecaEstoqueCardView(
            Long id,
            String descricaoModelo,
            String skuBase,
            String tamanho,
            String fotoMockupPath,
            Integer quantidadeDisponivel,
            Integer quantidadeOriginalProduzida,
            String cpvFormatado,
            BigDecimal cpvValor,
            String precoSugerido25Formatado,
            BigDecimal precoSugerido25Valor,
            String precoSugerido35Formatado,
            BigDecimal precoSugerido35Valor
    ) {}

    public record VendaLinhaView(
            String timestamp,
            String codigoVenda,
            String canalVendaDescricao,
            String canalVendaCode,
            String formaPagamentoDescricao,
            String descricaoProduto,
            String tamanho,
            Integer quantidade,
            String precoUnitarioFormatado,
            String valorTotalFormatado,
            String cpvTotalFormatado,
            String lucroBrutoFormatado,
            String margemPercentualFormatada,
            String nomeCliente
    ) {}
}
