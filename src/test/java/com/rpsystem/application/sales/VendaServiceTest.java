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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VendaServiceTest {

    @Mock
    private VendaRepository vendaRepository;

    @Mock
    private OrdemProducaoRepository ordemProducaoRepository;

    @Mock
    private ContaFinanceiraRepository contaFinanceiraRepository;

    @Mock
    private FluxoCaixaService fluxoCaixaService;

    @InjectMocks
    private VendaService vendaService;

    private OrdemProducao ordemProducao;
    private ContaFinanceira contaPadrao;

    @BeforeEach
    void setUp() {
        ordemProducao = new OrdemProducao();
        ordemProducao.setId(1L);
        ordemProducao.setDescricaoModelo("Camiseta Streetwear Drop 1");
        ordemProducao.setQuantidadeProduzir(10);
        ordemProducao.setQuantidadeDisponivel(10);
        ordemProducao.setCpvUnitarioFinal(new BigDecimal("40.00"));
        ordemProducao.setCpvTotal(new BigDecimal("400.00"));

        contaPadrao = new ContaFinanceira("Caixa Operacional", TipoConta.CAIXA_INTERNO, BigDecimal.ZERO);
        contaPadrao.setId(1L);
    }

    @Test
    void deveRegistrarVendaComSucessoEDiminuirEstoque() {
        when(ordemProducaoRepository.findById(1L)).thenReturn(Optional.of(ordemProducao));
        when(contaFinanceiraRepository.findFirstByAtivoTrueOrderByIdAsc()).thenReturn(Optional.of(contaPadrao));
        when(vendaRepository.save(any(Venda.class))).thenAnswer(invocation -> invocation.getArgument(0));

        VendaCreateRequest request = new VendaCreateRequest();
        request.setOrdemProducaoId(1L);
        request.setQuantidade(2);
        request.setPrecoVendaUnitario(new BigDecimal("110.00"));
        request.setCanalVenda(CanalVenda.DIRETA_BOCA_A_BOCA);
        request.setFormaPagamento(FormaPagamento.PIX);
        request.setNomeCliente("Lucas");

        Venda venda = vendaService.registrarVenda(request);

        assertNotNull(venda);
        assertEquals(8, ordemProducao.getQuantidadeDisponivel(), "O saldo disponível deve ser decrementado de 10 para 8");
        assertEquals(2, venda.getQuantidade());
        assertEquals(new BigDecimal("110.00"), venda.getPrecoVendaUnitario());
        assertEquals(new BigDecimal("220.00"), venda.getValorTotal());
        assertEquals(new BigDecimal("40.00"), venda.getCpvUnitario());
        assertEquals(new BigDecimal("80.00"), venda.getCpvTotal());
        assertEquals(new BigDecimal("140.00"), venda.getLucroBruto());
        assertEquals(new BigDecimal("63.64"), venda.getMargemPercentual());
        assertTrue(venda.getCodigoVenda().startsWith("VND-"));

        verify(ordemProducaoRepository).save(ordemProducao);
        verify(vendaRepository).save(any(Venda.class));
        verify(fluxoCaixaService).registrarVenda(any(Venda.class));
    }

    @Test
    void deveLancarExcecaoQuandoEstoqueInsuficiente() {
        ordemProducao.setQuantidadeDisponivel(1);
        when(ordemProducaoRepository.findById(1L)).thenReturn(Optional.of(ordemProducao));

        VendaCreateRequest request = new VendaCreateRequest();
        request.setOrdemProducaoId(1L);
        request.setQuantidade(2);
        request.setPrecoVendaUnitario(new BigDecimal("110.00"));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            vendaService.registrarVenda(request);
        });

        assertTrue(ex.getMessage().contains("Saldo insuficiente em estoque"));
        assertEquals(1, ordemProducao.getQuantidadeDisponivel());
        verify(vendaRepository, never()).save(any());
        verify(fluxoCaixaService, never()).registrarVenda(any());
    }
}
