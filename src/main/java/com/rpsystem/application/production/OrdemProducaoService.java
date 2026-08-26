package com.rpsystem.application.production;

import com.rpsystem.domain.finance.model.EstoqueFluidoDtf;
import com.rpsystem.domain.finance.repository.EstoqueFluidoDtfRepository;
import com.rpsystem.domain.inventory.model.LoteBlankItem;
import com.rpsystem.domain.inventory.repository.LoteBlankItemRepository;
import com.rpsystem.domain.production.model.OrdemProducao;
import com.rpsystem.domain.production.model.OrdemProducaoConsumoBlank;
import com.rpsystem.domain.production.model.OrdemProducaoConsumoDtf;
import com.rpsystem.domain.production.model.OrdemProducaoFragmento;
import com.rpsystem.domain.production.repository.OrdemProducaoRepository;
import com.rpsystem.presentation.request.OrdemProducaoCreateRequest;
import com.rpsystem.application.finance.FluxoCaixaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class OrdemProducaoService {

    private static final int SCALE = 6;
    private static final long MAX_MOCKUP_SIZE_BYTES = 10L * 1024L * 1024L;
    private static final Set<String> MOCKUP_CONTENT_TYPES = Set.of("image/png", "image/jpeg", "image/webp");

    private final OrdemProducaoRepository ordemProducaoRepository;
    private final LoteBlankItemRepository loteBlankItemRepository;
    private final EstoqueFluidoDtfRepository estoqueFluidoDtfRepository;
    private final FluxoCaixaService fluxoCaixaService;

    public OrdemProducaoService(
            OrdemProducaoRepository ordemProducaoRepository,
            LoteBlankItemRepository loteBlankItemRepository,
            EstoqueFluidoDtfRepository estoqueFluidoDtfRepository,
            FluxoCaixaService fluxoCaixaService
    ) {
        this.ordemProducaoRepository = ordemProducaoRepository;
        this.loteBlankItemRepository = loteBlankItemRepository;
        this.estoqueFluidoDtfRepository = estoqueFluidoDtfRepository;
        this.fluxoCaixaService = fluxoCaixaService;
    }

    @Transactional
    public OrdemProducao registrar(OrdemProducaoCreateRequest request) {
        LoteBlankItem itemSelecionado = loteBlankItemRepository.findById(request.getSkuBlankId())
                .orElseThrow(() -> new IllegalArgumentException("SKU de blank não encontrado."));

        if (request.getQuantidadeProduzir() > itemSelecionado.getQuantidadeDisponivel()) {
            throw new IllegalArgumentException("A quantidade a produzir não pode ser maior que o saldo disponível do SKU selecionado.");
        }

        BigDecimal fatorAproveitamento = request.getFatorAproveitamentoPerc()
                .divide(BigDecimal.valueOf(100), SCALE, RoundingMode.HALF_UP);
        if (fatorAproveitamento.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Fator de aproveitamento precisa ser maior que zero.");
        }

        BigDecimal areaNominalPorPeca = request.getFragmentos().stream()
                .map(fragmento -> fragmento.getLargura().multiply(fragmento.getAltura()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal areaRealPorPeca = areaNominalPorPeca.divide(fatorAproveitamento, SCALE, RoundingMode.HALF_UP);
        BigDecimal areaRealTotal = areaRealPorPeca.multiply(BigDecimal.valueOf(request.getQuantidadeProduzir()));

        EstoqueFluidoDtf estoque = estoqueFluidoDtfRepository.findTopByOrderByIdDesc()
                .orElseThrow(() -> new IllegalStateException("Não existe estoque DTF configurado."));

        if (estoque.getAreaTotalCm2().compareTo(areaRealTotal) < 0) {
            throw new IllegalStateException("Saldo de DTF insuficiente para a ordem de produção.");
        }

        List<LoteBlankItem> lotesDisponiveis = loteBlankItemRepository
            .findDisponiveisParaSku(
                        itemSelecionado.getSkuBase(),
                        itemSelecionado.getTamanho(),
                        0
                );

        if (lotesDisponiveis.isEmpty()) {
            throw new IllegalStateException("Não há saldo disponível para o SKU selecionado.");
        }

        OrdemProducao ordem = new OrdemProducao();
        ordem.setDescricaoModelo(request.getDescricaoModelo());
        ordem.setLoteBlankItem(itemSelecionado);
        ordem.setQuantidadeProduzir(request.getQuantidadeProduzir());
        ordem.setOperadorResponsavel(request.getOperadorResponsavel());
        ordem.setFatorAproveitamentoDtfPerc(request.getFatorAproveitamentoPerc());
        ordem.setRegistradaEm(LocalDateTime.now());
        ordem.setFotoMockupPath(salvarMockupObrigatorio(request.getFotoMockup()));

        for (OrdemProducaoCreateRequest.FragmentoRequest fragmentoRequest : request.getFragmentos()) {
            OrdemProducaoFragmento fragmento = new OrdemProducaoFragmento();
            fragmento.setDescricao(fragmentoRequest.getDescricao());
            fragmento.setLarguraCm(fragmentoRequest.getLargura());
            fragmento.setAlturaCm(fragmentoRequest.getAltura());
            fragmento.setAreaCm2(fragmentoRequest.getLargura().multiply(fragmentoRequest.getAltura()));
            ordem.adicionarFragmento(fragmento);
        }

        int quantidadeRestante = request.getQuantidadeProduzir();
        BigDecimal custoBlanksTotal = BigDecimal.ZERO;

        for (LoteBlankItem loteDisponivel : lotesDisponiveis) {
            if (quantidadeRestante <= 0) {
                break;
            }

            int quantidadeConsumida = Math.min(quantidadeRestante, loteDisponivel.getQuantidadeDisponivel());
            BigDecimal custoTotalItem = loteDisponivel.getCustoComFreteCalculado()
                    .multiply(BigDecimal.valueOf(quantidadeConsumida));

            loteDisponivel.setQuantidadeDisponivel(loteDisponivel.getQuantidadeDisponivel() - quantidadeConsumida);
            custoBlanksTotal = custoBlanksTotal.add(custoTotalItem);
            quantidadeRestante -= quantidadeConsumida;

            OrdemProducaoConsumoBlank consumoBlank = new OrdemProducaoConsumoBlank();
            consumoBlank.setLoteBlankItem(loteDisponivel);
            consumoBlank.setQuantidadeConsumida(quantidadeConsumida);
            consumoBlank.setCustoUnitarioAplicado(loteDisponivel.getCustoComFreteCalculado());
            consumoBlank.setCustoTotal(custoTotalItem.setScale(2, RoundingMode.HALF_UP));
            ordem.adicionarConsumoBlank(consumoBlank);
        }

        if (quantidadeRestante > 0) {
            throw new IllegalStateException("Saldo de blanks insuficiente para concluir a ordem.");
        }

        BigDecimal custoMedioDtf = estoque.getPrecoMedioPorCm2();
        BigDecimal custoDtfTotal = areaRealTotal.multiply(custoMedioDtf).setScale(2, RoundingMode.HALF_UP);

        estoque.setAreaTotalCm2(estoque.getAreaTotalCm2().subtract(areaRealTotal));
        estoque.setCustoTotalAcumulado(estoque.getAreaTotalCm2().multiply(custoMedioDtf).setScale(2, RoundingMode.HALF_UP));
        estoque.setAtualizadoEm(LocalDateTime.now());

        OrdemProducaoConsumoDtf consumoDtf = new OrdemProducaoConsumoDtf();
        consumoDtf.setAreaConsumidaCm2(areaRealTotal.setScale(2, RoundingMode.HALF_UP));
        consumoDtf.setCustoMedioAplicado(custoMedioDtf);
        consumoDtf.setCustoTotal(custoDtfTotal);
        ordem.adicionarConsumoDtf(consumoDtf);

        BigDecimal custoOperacionalTotal = BigDecimal.ZERO;
        BigDecimal cpvTotal = custoBlanksTotal.add(custoDtfTotal).add(custoOperacionalTotal).setScale(2, RoundingMode.HALF_UP);
        BigDecimal cpvUnitarioFinal = cpvTotal.divide(BigDecimal.valueOf(request.getQuantidadeProduzir()), 2, RoundingMode.HALF_UP);

        ordem.setCustoBlanksTotal(custoBlanksTotal.setScale(2, RoundingMode.HALF_UP));
        ordem.setCustoDtfTotal(custoDtfTotal);
        ordem.setCustoOperacionalTotal(custoOperacionalTotal);
        ordem.setCpvTotal(cpvTotal);
        ordem.setCpvUnitarioFinal(cpvUnitarioFinal);

        estoqueFluidoDtfRepository.save(estoque);
        OrdemProducao salva = ordemProducaoRepository.save(ordem);
        fluxoCaixaService.registrarOrdemProducao(salva, areaNominalPorPeca, areaRealTotal, custoMedioDtf);
        return salva;
    }

    private String salvarMockupObrigatorio(MultipartFile arquivo) {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new IllegalArgumentException("O mockup digital da peça é obrigatório.");
        }

        if (!MOCKUP_CONTENT_TYPES.contains(arquivo.getContentType())) {
            throw new IllegalArgumentException("Formato de mockup inválido. Use PNG, JPG/JPEG ou WEBP.");
        }

        if (arquivo.getSize() > MAX_MOCKUP_SIZE_BYTES) {
            throw new IllegalArgumentException("O mockup excede o tamanho máximo de 10MB.");
        }

        try {
            Path diretorio = Paths.get("uploads", "mockups");
            Files.createDirectories(diretorio);

            String nomeOriginal = arquivo.getOriginalFilename() == null ? "mockup" : arquivo.getOriginalFilename();
            String nomeSeguro = nomeOriginal.replaceAll("[^a-zA-Z0-9._-]", "_");
            String nomeArquivo = UUID.randomUUID() + "-" + nomeSeguro;
            Path destino = diretorio.resolve(nomeArquivo);

            Files.copy(arquivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/mockups/" + nomeArquivo;
        } catch (IOException exception) {
            throw new IllegalStateException("Falha ao armazenar o mockup da ordem.", exception);
        }
    }
}