package com.rpsystem.application.finance;

import com.rpsystem.domain.finance.model.EntradaDtf;
import com.rpsystem.domain.finance.model.EstoqueFluidoDtf;
import com.rpsystem.domain.finance.repository.EntradaDtfRepository;
import com.rpsystem.domain.finance.repository.EstoqueFluidoDtfRepository;
import com.rpsystem.presentation.request.EntradaDtfCreateRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
public class EntradaDtfService {

    private static final int SCALE = 6;

    private final EstoqueFluidoDtfRepository estoqueFluidoDtfRepository;
    private final EntradaDtfRepository entradaDtfRepository;
    private final FluxoCaixaService fluxoCaixaService;

    public EntradaDtfService(
            EstoqueFluidoDtfRepository estoqueFluidoDtfRepository,
            EntradaDtfRepository entradaDtfRepository,
            FluxoCaixaService fluxoCaixaService
    ) {
        this.estoqueFluidoDtfRepository = estoqueFluidoDtfRepository;
        this.entradaDtfRepository = entradaDtfRepository;
        this.fluxoCaixaService = fluxoCaixaService;
    }

    @Transactional
    public EntradaDtf registrar(EntradaDtfCreateRequest request) {
        BigDecimal areaLoteCm2 = request.getLargura().multiply(request.getComprimento());
        BigDecimal valorTotalGasto = request.getValorNota().add(request.getCustoLogistico());

        EstoqueFluidoDtf estoque = estoqueFluidoDtfRepository.findTopByOrderByIdDesc().orElseGet(() -> {
            EstoqueFluidoDtf novo = new EstoqueFluidoDtf();
            novo.setAreaTotalCm2(BigDecimal.ZERO);
            novo.setCustoTotalAcumulado(BigDecimal.ZERO);
            novo.setPrecoMedioPorCm2(BigDecimal.ZERO);
            novo.setAtualizadoEm(LocalDateTime.now());
            return novo;
        });

        BigDecimal saldoAnteriorArea = estoque.getAreaTotalCm2() == null ? BigDecimal.ZERO : estoque.getAreaTotalCm2();
        BigDecimal precoMedioAnterior = estoque.getPrecoMedioPorCm2() == null ? BigDecimal.ZERO : estoque.getPrecoMedioPorCm2();
        BigDecimal saldoPosteriorArea = saldoAnteriorArea.add(areaLoteCm2);
        BigDecimal custoAnterior = saldoAnteriorArea.multiply(precoMedioAnterior);
        BigDecimal novoCustoTotal = custoAnterior.add(valorTotalGasto);

        BigDecimal novoPrecoMedio = saldoPosteriorArea.compareTo(BigDecimal.ZERO) == 0
                ? BigDecimal.ZERO
                : novoCustoTotal.divide(saldoPosteriorArea, SCALE, RoundingMode.HALF_UP);

        estoque.setAreaTotalCm2(saldoPosteriorArea);
        estoque.setCustoTotalAcumulado(novoCustoTotal);
        estoque.setPrecoMedioPorCm2(novoPrecoMedio);
        estoque.setAtualizadoEm(LocalDateTime.now());

        estoque = estoqueFluidoDtfRepository.save(estoque);

        EntradaDtf entrada = new EntradaDtf();
        entrada.setLarguraCm(request.getLargura());
        entrada.setComprimentoCm(request.getComprimento());
        entrada.setAreaLoteCm2(areaLoteCm2);
        entrada.setValorNota(request.getValorNota());
        entrada.setCustoLogistico(request.getCustoLogistico());
        entrada.setValorTotalGasto(valorTotalGasto);
        entrada.setSaldoAreaAnteriorCm2(saldoAnteriorArea);
        entrada.setSaldoAreaPosteriorCm2(saldoPosteriorArea);
        entrada.setPrecoMedioAnteriorCm2(precoMedioAnterior);
        entrada.setPrecoMedioPosteriorCm2(novoPrecoMedio);
        entrada.setRegistradoEm(LocalDateTime.now());
        entrada.setEstoqueFluidoDtf(estoque);

        EntradaDtf salva = entradaDtfRepository.save(entrada);
        fluxoCaixaService.registrarEntradaDtf(salva);
        return salva;
    }
}