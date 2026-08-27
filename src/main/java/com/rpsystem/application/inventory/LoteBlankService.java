package com.rpsystem.application.inventory;

import com.rpsystem.domain.inventory.model.LoteBlank;
import com.rpsystem.domain.inventory.model.LoteBlankItem;
import com.rpsystem.domain.inventory.repository.LoteBlankRepository;
import com.rpsystem.application.finance.FluxoCaixaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class LoteBlankService {

    private static final int SCALE = 6;

    private final LoteBlankRepository loteBlankRepository;
    private final FluxoCaixaService fluxoCaixaService;

    public LoteBlankService(LoteBlankRepository loteBlankRepository, FluxoCaixaService fluxoCaixaService) {
        this.loteBlankRepository = loteBlankRepository;
        this.fluxoCaixaService = fluxoCaixaService;
    }

    @Transactional
    public LoteBlank registrar(LoteBlank loteBlank) {
        loteBlank.setItens(new ArrayList<>(consolidarItensRepetidos(loteBlank.getItens())));
        validarLote(loteBlank);

        BigDecimal custoTotalItens = loteBlank.getItens().stream()
                .map(item -> item.getPrecoNotaUnitario().multiply(BigDecimal.valueOf(item.getQuantidadeAdquirida())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (custoTotalItens.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O lote precisa ter valor total de itens maior que zero.");
        }

        BigDecimal freteTotal = loteBlank.getValorFreteTotal() != null ? loteBlank.getValorFreteTotal() : BigDecimal.ZERO;

        for (LoteBlankItem item : loteBlank.getItens()) {
            item.setLoteBlank(loteBlank);
            item.setQuantidadeDisponivel(item.getQuantidadeAdquirida());

            BigDecimal subtotalItem = item.getPrecoNotaUnitario()
                    .multiply(BigDecimal.valueOf(item.getQuantidadeAdquirida()));
            BigDecimal proporcaoValor = subtotalItem.divide(custoTotalItens, SCALE, RoundingMode.HALF_UP);
            BigDecimal freteRateadoItem = freteTotal.multiply(proporcaoValor);
            BigDecimal freteUnitario = freteRateadoItem.divide(BigDecimal.valueOf(item.getQuantidadeAdquirida()), SCALE, RoundingMode.HALF_UP);

            item.setCustoComFreteCalculado(item.getPrecoNotaUnitario().add(freteUnitario).setScale(2, RoundingMode.HALF_UP));
        }

        LoteBlank salvo = loteBlankRepository.save(loteBlank);
        fluxoCaixaService.registrarEntradaBlanks(salvo);
        return salvo;
    }

    private List<LoteBlankItem> consolidarItensRepetidos(List<LoteBlankItem> itens) {
        if (itens == null) {
            return new ArrayList<>();
        }
        Map<String, LoteBlankItem> itensConsolidados = new LinkedHashMap<>();

        for (LoteBlankItem item : itens) {
            if (item == null) continue;
            String chave = montarChave(item);
            LoteBlankItem consolidado = itensConsolidados.get(chave);

            if (consolidado == null) {
                itensConsolidados.put(chave, item);
                continue;
            }

            int quantidadeExistente = consolidado.getQuantidadeAdquirida() != null ? consolidado.getQuantidadeAdquirida() : 0;
            int quantidadeNova = item.getQuantidadeAdquirida() != null ? item.getQuantidadeAdquirida() : 0;
            BigDecimal precoExistente = consolidado.getPrecoNotaUnitario() != null ? consolidado.getPrecoNotaUnitario() : BigDecimal.ZERO;
            BigDecimal precoNovo = item.getPrecoNotaUnitario() != null ? item.getPrecoNotaUnitario() : BigDecimal.ZERO;

            BigDecimal valorTotalExistente = precoExistente.multiply(BigDecimal.valueOf(quantidadeExistente));
            BigDecimal valorTotalNovo = precoNovo.multiply(BigDecimal.valueOf(quantidadeNova));

            int quantidadeConsolidada = quantidadeExistente + quantidadeNova;
            if (quantidadeConsolidada > 0) {
                BigDecimal valorConsolidado = valorTotalExistente.add(valorTotalNovo);
                BigDecimal novoPrecoMedio = valorConsolidado.divide(BigDecimal.valueOf(quantidadeConsolidada), SCALE, RoundingMode.HALF_UP);

                consolidado.setQuantidadeAdquirida(quantidadeConsolidada);
                consolidado.setPrecoNotaUnitario(novoPrecoMedio.setScale(2, RoundingMode.HALF_UP));
            }
        }

        return new ArrayList<>(itensConsolidados.values());
    }

    private String montarChave(LoteBlankItem item) {
        String skuBase = item.getSkuBase() == null ? "" : item.getSkuBase().trim().toUpperCase(Locale.ROOT);
        String tamanho = item.getTamanho() == null ? "" : item.getTamanho().trim().toUpperCase(Locale.ROOT);
        return skuBase + "|" + tamanho;
    }

    private void validarLote(LoteBlank loteBlank) {
        if (loteBlank == null) {
            throw new IllegalArgumentException("Lote inválido.");
        }

        if (loteBlank.getItens() == null || loteBlank.getItens().isEmpty()) {
            throw new IllegalArgumentException("Informe ao menos um item para o lote de blanks.");
        }

        for (LoteBlankItem item : loteBlank.getItens()) {
            if (item.getQuantidadeAdquirida() == null || item.getQuantidadeAdquirida() <= 0) {
                throw new IllegalArgumentException("A quantidade de cada item deve ser maior que zero.");
            }

            if (item.getPrecoNotaUnitario() == null || item.getPrecoNotaUnitario().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("O preço unitário precisa ser informado.");
            }
        }
    }
}