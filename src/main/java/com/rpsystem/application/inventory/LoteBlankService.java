package com.rpsystem.application.inventory;

import com.rpsystem.domain.inventory.model.LoteBlank;
import com.rpsystem.domain.inventory.model.LoteBlankItem;
import com.rpsystem.domain.inventory.repository.LoteBlankRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class LoteBlankService {

    private static final int SCALE = 6;

    private final LoteBlankRepository loteBlankRepository;

    public LoteBlankService(LoteBlankRepository loteBlankRepository) {
        this.loteBlankRepository = loteBlankRepository;
    }

    @Transactional
    public LoteBlank registrar(LoteBlank loteBlank) {
        loteBlank.setItens(consolidarItensRepetidos(loteBlank.getItens()));
        validarLote(loteBlank);

        BigDecimal custoTotalItens = loteBlank.getItens().stream()
                .map(item -> item.getPrecoNotaUnitario().multiply(BigDecimal.valueOf(item.getQuantidadeAdquirida())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (custoTotalItens.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O lote precisa ter valor total de itens maior que zero.");
        }

        for (LoteBlankItem item : loteBlank.getItens()) {
            item.setLoteBlank(loteBlank);
            item.setQuantidadeDisponivel(item.getQuantidadeAdquirida());

            BigDecimal subtotalItem = item.getPrecoNotaUnitario()
                    .multiply(BigDecimal.valueOf(item.getQuantidadeAdquirida()));
            BigDecimal proporcaoValor = subtotalItem.divide(custoTotalItens, SCALE, RoundingMode.HALF_UP);
            BigDecimal freteRateadoItem = loteBlank.getValorFreteTotal().multiply(proporcaoValor);
            BigDecimal freteUnitario = freteRateadoItem.divide(BigDecimal.valueOf(item.getQuantidadeAdquirida()), SCALE, RoundingMode.HALF_UP);

            item.setCustoComFreteCalculado(item.getPrecoNotaUnitario().add(freteUnitario).setScale(2, RoundingMode.HALF_UP));
        }

        return loteBlankRepository.save(loteBlank);
    }

    private List<LoteBlankItem> consolidarItensRepetidos(List<LoteBlankItem> itens) {
        Map<String, LoteBlankItem> itensConsolidados = new LinkedHashMap<>();

        for (LoteBlankItem item : itens) {
            String chave = montarChave(item);
            LoteBlankItem consolidado = itensConsolidados.get(chave);

            if (consolidado == null) {
                itensConsolidados.put(chave, item);
                continue;
            }

            int quantidadeExistente = consolidado.getQuantidadeAdquirida();
            int quantidadeNova = item.getQuantidadeAdquirida();
            BigDecimal valorTotalExistente = consolidado.getPrecoNotaUnitario()
                    .multiply(BigDecimal.valueOf(quantidadeExistente));
            BigDecimal valorTotalNovo = item.getPrecoNotaUnitario()
                    .multiply(BigDecimal.valueOf(quantidadeNova));

            int quantidadeConsolidada = quantidadeExistente + quantidadeNova;
            BigDecimal valorConsolidado = valorTotalExistente.add(valorTotalNovo);
            BigDecimal novoPrecoMedio = valorConsolidado.divide(BigDecimal.valueOf(quantidadeConsolidada), SCALE, RoundingMode.HALF_UP);

            consolidado.setQuantidadeAdquirida(quantidadeConsolidada);
            consolidado.setPrecoNotaUnitario(novoPrecoMedio.setScale(2, RoundingMode.HALF_UP));
        }

        return List.copyOf(itensConsolidados.values());
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