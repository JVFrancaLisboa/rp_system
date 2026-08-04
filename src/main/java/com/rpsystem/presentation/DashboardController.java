package com.rpsystem.presentation;

import com.rpsystem.domain.finance.model.EstoqueFluidoDtf;
import com.rpsystem.domain.finance.repository.EstoqueFluidoDtfRepository;
import com.rpsystem.domain.inventory.repository.LoteBlankItemRepository;
import com.rpsystem.domain.inventory.repository.LoteBlankRepository;
import com.rpsystem.domain.production.model.OrdemProducao;
import com.rpsystem.domain.production.repository.OrdemProducaoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Controller
public class DashboardController {

    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");
    private static final DateTimeFormatter DATA_HORA_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final LoteBlankRepository loteBlankRepository;
    private final LoteBlankItemRepository loteBlankItemRepository;
    private final EstoqueFluidoDtfRepository estoqueFluidoDtfRepository;
    private final OrdemProducaoRepository ordemProducaoRepository;

    public DashboardController(
            LoteBlankRepository loteBlankRepository,
            LoteBlankItemRepository loteBlankItemRepository,
            EstoqueFluidoDtfRepository estoqueFluidoDtfRepository,
            OrdemProducaoRepository ordemProducaoRepository
    ) {
        this.loteBlankRepository = loteBlankRepository;
        this.loteBlankItemRepository = loteBlankItemRepository;
        this.estoqueFluidoDtfRepository = estoqueFluidoDtfRepository;
        this.ordemProducaoRepository = ordemProducaoRepository;
    }

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        long totalPecasDisponiveis = loteBlankItemRepository.somaQuantidadeDisponivel();
        long totalLotes = loteBlankRepository.totalLotes();
        long totalOrdens = ordemProducaoRepository.count();

        EstoqueFluidoDtf estoque = estoqueFluidoDtfRepository.findTopByOrderByIdDesc().orElse(null);
        OrdemProducao ultimaOrdem = ordemProducaoRepository.findTopByOrderByRegistradaEmDescIdDesc().orElse(null);
        List<OrdemProducao> ordensRecentes = ordemProducaoRepository.findTop12ByOrderByRegistradaEmDescIdDesc();

        BigDecimal saldoDtf = estoque != null && estoque.getAreaTotalCm2() != null ? estoque.getAreaTotalCm2() : BigDecimal.ZERO;
        BigDecimal custoMedioDtf = estoque != null && estoque.getPrecoMedioPorCm2() != null ? estoque.getPrecoMedioPorCm2() : BigDecimal.ZERO;
        BigDecimal cpvMedioAtual = ultimaOrdem != null && ultimaOrdem.getCpvUnitarioFinal() != null ? ultimaOrdem.getCpvUnitarioFinal() : BigDecimal.ZERO;
        NumberFormat moedaFormat = NumberFormat.getCurrencyInstance(PT_BR);
        List<OrdemResumoCard> ordensRecentesCards = ordensRecentes.stream()
            .map(ordem -> new OrdemResumoCard(
                ordem.getFotoMockupPath() != null ? ordem.getFotoMockupPath() : "/images/logo.png",
                ordem.getDescricaoModelo(),
                ordem.getOperadorResponsavel(),
                moedaFormat.format(ordem.getCpvUnitarioFinal() != null ? ordem.getCpvUnitarioFinal() : BigDecimal.ZERO),
                moedaFormat.format(ordem.getCpvTotal() != null ? ordem.getCpvTotal() : BigDecimal.ZERO),
                moedaFormat.format((ordem.getCpvUnitarioFinal() != null ? ordem.getCpvUnitarioFinal() : BigDecimal.ZERO).multiply(BigDecimal.valueOf(2.5))),
                moedaFormat.format((ordem.getCpvUnitarioFinal() != null ? ordem.getCpvUnitarioFinal() : BigDecimal.ZERO).multiply(BigDecimal.valueOf(3.5))),
                (ordem.getQuantidadeProduzir() != null ? ordem.getQuantidadeProduzir() : 0) + " pcs",
                ordem.getRegistradaEm() != null ? DATA_HORA_FORMATTER.format(ordem.getRegistradaEm()) : "--/--/---- --:--"
            ))
            .toList();

        model.addAttribute("totalPecasDisponiveis", totalPecasDisponiveis);
        model.addAttribute("totalLotes", totalLotes);
        model.addAttribute("totalOrdens", totalOrdens);
        model.addAttribute("saldoDtf", saldoDtf);
        model.addAttribute("custoMedioDtf", custoMedioDtf);
        model.addAttribute("cpvMedioAtual", cpvMedioAtual);
        model.addAttribute("ultimaOrdem", ultimaOrdem);
        model.addAttribute("ordensRecentes", ordensRecentesCards);
        model.addAttribute("saldoDtfFormatado", formatarNumero(saldoDtf, 0));
        model.addAttribute("custoMedioDtfFormatado", formatarMoeda(custoMedioDtf));
        model.addAttribute("cpvMedioAtualFormatado", formatarMoeda(cpvMedioAtual));
        model.addAttribute("cpvMedioAtualNumeroFormatado", formatarNumero(cpvMedioAtual, 2));

        return "dashboard";
    }

    private String formatarMoeda(BigDecimal valor) {
        return NumberFormat.getCurrencyInstance(PT_BR).format(valor);
    }

    private String formatarNumero(BigDecimal valor, int casasDecimais) {
        NumberFormat formatador = NumberFormat.getNumberInstance(PT_BR);
        formatador.setMinimumFractionDigits(casasDecimais);
        formatador.setMaximumFractionDigits(casasDecimais);
        return formatador.format(valor);
    }

    public record OrdemResumoCard(
            String fotoMockupPath,
            String descricaoModelo,
            String operadorResponsavel,
            String cpvUnitarioFinalFormatado,
            String cpvTotalFormatado,
            String precoFinalMultiplicador25,
            String precoFinalMultiplicador35,
            String quantidadeProduzidaFormatada,
            String registradaEmFormatada
    ) {
    }
}