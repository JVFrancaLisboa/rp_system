package com.rpsystem.presentation;

import com.rpsystem.application.finance.FluxoCaixaService;
import com.rpsystem.domain.finance.model.LedgerEstoqueMovimento;
import com.rpsystem.domain.finance.model.TipoMovimento;
import com.rpsystem.domain.finance.model.TransacaoFinanceira;
import com.rpsystem.presentation.request.LancamentoManualRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Controller
public class FluxoCaixaController {

    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");
    private static final DateTimeFormatter DATA_HORA_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final FluxoCaixaService fluxoCaixaService;

    public FluxoCaixaController(FluxoCaixaService fluxoCaixaService) {
        this.fluxoCaixaService = fluxoCaixaService;
    }

    @GetMapping("/fluxo-caixa")
    public String fluxoCaixa(Model model) {
        FluxoCaixaService.KpisFluxoCaixa kpis = fluxoCaixaService.obterKpis();
        List<TransacaoFinanceira> transacoes = fluxoCaixaService.listarTransacoesRecentes();
        List<LedgerEstoqueMovimento> movimentacoesFisicas = fluxoCaixaService.listarMovimentacoesFisicasRecentes();

        NumberFormat moedaFormat = NumberFormat.getCurrencyInstance(PT_BR);

        List<TransacaoResumoView> transacoesViews = transacoes.stream()
                .map(tx -> {
                    String timestamp = tx.getDataCompetencia() != null
                            ? DATA_HORA_FORMATTER.format(tx.getDataCompetencia())
                            : "--/--/---- --:--:--";
                    String debito = tx.getTipoMovimento() == TipoMovimento.SAIDA
                            ? "- " + moedaFormat.format(tx.getValor())
                            : "-";
                    String credito = tx.getTipoMovimento() == TipoMovimento.ENTRADA
                            ? "+ " + moedaFormat.format(tx.getValor())
                            : "-";
                    String cpv = tx.getCpvHistorico() != null
                            ? moedaFormat.format(tx.getCpvHistorico())
                            : "-";
                    String margem = "-";
                    if (tx.getMargemNominal() != null && tx.getValor() != null && tx.getValor().compareTo(BigDecimal.ZERO) > 0) {
                        BigDecimal perc = tx.getMargemNominal()
                                .multiply(BigDecimal.valueOf(100))
                                .divide(tx.getValor(), 1, RoundingMode.HALF_UP);
                        margem = moedaFormat.format(tx.getMargemNominal()) + " (" + perc + "%)";
                    }

                    return new TransacaoResumoView(
                            timestamp,
                            tx.getCodigoTransacao(),
                            tx.getTipoEvento().getDescricao(),
                            tx.getTipoEvento().name(),
                            tx.getTipoMovimento() == TipoMovimento.ENTRADA,
                            tx.getDescricao(),
                            debito,
                            credito,
                            cpv,
                            margem,
                            tx.getCategoriaFinanceira() != null ? tx.getCategoriaFinanceira().getNome() : "Geral",
                            tx.getCategoriaFinanceira() != null && tx.getCategoriaFinanceira().getCorHex() != null
                                    ? tx.getCategoriaFinanceira().getCorHex()
                                    : "#58a6ff"
                    );
                })
                .toList();

        List<MovimentoFisicoView> fisicosViews = movimentacoesFisicas.stream()
                .map(mov -> {
                    String timestamp = mov.getDataHora() != null
                            ? DATA_HORA_FORMATTER.format(mov.getDataHora())
                            : "--/--/---- --:--:--";
                    boolean isEntrada = mov.getQuantidadeDelta() != null && mov.getQuantidadeDelta().compareTo(BigDecimal.ZERO) >= 0;

                    return new MovimentoFisicoView(
                            timestamp,
                            mov.getCodigoEvento(),
                            mov.getTipoOperacao().getDescricao(),
                            mov.getTipoOperacao().name(),
                            isEntrada,
                            mov.getReferenciaMatriz(),
                            mov.getVariacaoVolumetrica(),
                            mov.getImpactoDetalhado(),
                            moedaFormat.format(mov.getCustoTotalImpactado() != null ? mov.getCustoTotalImpactado() : BigDecimal.ZERO)
                    );
                })
                .toList();

        model.addAttribute("kpis", kpis);
        model.addAttribute("margemLiquidaFormatada", moedaFormat.format(kpis.margemContribuicaoLiquida()));
        model.addAttribute("refugoFinanceiroFormatado", moedaFormat.format(kpis.refugoFinanceiroTotal()));
        model.addAttribute("saldoDtfFormatado", formatarNumero(kpis.saldoDtfCm2(), 0));
        model.addAttribute("custoMedioDtfFormatado", formatarCustoDtf(kpis.precoMedioDtfPorCm2()));
        model.addAttribute("saldoConsolidadoFormatado", moedaFormat.format(kpis.saldoConsolidadoCaixa()));
        model.addAttribute("totalReceitasFormatado", moedaFormat.format(kpis.totalReceitas()));
        model.addAttribute("totalDespesasFormatado", moedaFormat.format(kpis.totalDespesas()));

        model.addAttribute("transacoes", transacoesViews);
        model.addAttribute("movimentacoesFisicas", fisicosViews);
        model.addAttribute("contas", fluxoCaixaService.listarContas());
        model.addAttribute("categorias", fluxoCaixaService.listarCategorias());

        return "fluxo-caixa";
    }

    @PostMapping("/api/v1/financeiro/transacoes")
    public String registrarTransacaoManual(@ModelAttribute LancamentoManualRequest request) {
        fluxoCaixaService.registrarTransacaoManual(request);
        return "redirect:/fluxo-caixa";
    }

    private String formatarNumero(BigDecimal valor, int casasDecimais) {
        if (valor == null) return "0";
        NumberFormat formatador = NumberFormat.getNumberInstance(PT_BR);
        formatador.setMinimumFractionDigits(casasDecimais);
        formatador.setMaximumFractionDigits(casasDecimais);
        return formatador.format(valor);
    }

    private String formatarCustoDtf(BigDecimal valor) {
        if (valor == null) return "R$ 0,000/cm²";
        NumberFormat formatador = NumberFormat.getNumberInstance(PT_BR);
        formatador.setMinimumFractionDigits(3);
        formatador.setMaximumFractionDigits(4);
        return "R$ " + formatador.format(valor) + "/cm²";
    }

    public record TransacaoResumoView(
            String timestamp,
            String codigoTransacao,
            String tipoEventoDescricao,
            String tipoEventoCode,
            boolean isEntrada,
            String descricao,
            String debitoFormatado,
            String creditoFormatado,
            String cpvHistoricoFormatado,
            String margemNominalFormatada,
            String categoriaNome,
            String categoriaCorHex
    ) {}

    public record MovimentoFisicoView(
            String timestamp,
            String codigoEvento,
            String tipoOperacaoDescricao,
            String tipoOperacaoCode,
            boolean isEntrada,
            String referenciaMatriz,
            String variacaoVolumetrica,
            String impactoDetalhado,
            String custoTotalFormatado
    ) {}
}
