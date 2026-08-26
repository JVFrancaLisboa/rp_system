package com.rpsystem.application.finance;

import com.rpsystem.domain.finance.model.CategoriaFinanceira;
import com.rpsystem.domain.finance.model.ContaFinanceira;
import com.rpsystem.domain.finance.model.EstoqueFluidoDtf;
import com.rpsystem.domain.finance.model.LedgerEstoqueMovimento;
import com.rpsystem.domain.finance.model.ModuloOrigem;
import com.rpsystem.domain.finance.model.StatusTransacao;
import com.rpsystem.domain.finance.model.TipoCategoriaFinanceira;
import com.rpsystem.domain.finance.model.TipoConta;
import com.rpsystem.domain.finance.model.TipoEventoFinanceiro;
import com.rpsystem.domain.finance.model.TipoInsumo;
import com.rpsystem.domain.finance.model.TipoMovimento;
import com.rpsystem.domain.finance.model.TipoOperacaoEstoque;
import com.rpsystem.domain.finance.model.TransacaoFinanceira;
import com.rpsystem.domain.finance.repository.CategoriaFinanceiraRepository;
import com.rpsystem.domain.finance.repository.ContaFinanceiraRepository;
import com.rpsystem.domain.finance.repository.EstoqueFluidoDtfRepository;
import com.rpsystem.domain.finance.repository.LedgerEstoqueMovimentoRepository;
import com.rpsystem.domain.finance.repository.TransacaoFinanceiraRepository;
import com.rpsystem.domain.finance.model.EntradaDtf;
import com.rpsystem.domain.inventory.model.LoteBlank;
import com.rpsystem.domain.inventory.model.LoteBlankItem;
import com.rpsystem.domain.production.model.OrdemProducao;
import com.rpsystem.presentation.request.LancamentoManualRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class FluxoCaixaService {

    private final TransacaoFinanceiraRepository transacaoRepository;
    private final LedgerEstoqueMovimentoRepository ledgerFisicoRepository;
    private final ContaFinanceiraRepository contaRepository;
    private final CategoriaFinanceiraRepository categoriaRepository;
    private final EstoqueFluidoDtfRepository estoqueFluidoDtfRepository;

    public FluxoCaixaService(
            TransacaoFinanceiraRepository transacaoRepository,
            LedgerEstoqueMovimentoRepository ledgerFisicoRepository,
            ContaFinanceiraRepository contaRepository,
            CategoriaFinanceiraRepository categoriaRepository,
            EstoqueFluidoDtfRepository estoqueFluidoDtfRepository
    ) {
        this.transacaoRepository = transacaoRepository;
        this.ledgerFisicoRepository = ledgerFisicoRepository;
        this.contaRepository = contaRepository;
        this.categoriaRepository = categoriaRepository;
        this.estoqueFluidoDtfRepository = estoqueFluidoDtfRepository;
    }

    @Transactional
    public void registrarEntradaBlanks(LoteBlank lote) {
        if (lote == null || lote.getItens() == null || lote.getItens().isEmpty()) {
            return;
        }

        ContaFinanceira conta = obterContaPadrao();
        CategoriaFinanceira catInsumo = obterOuCriarCategoria("Matéria-Prima (Blanks)", TipoCategoriaFinanceira.CUSTO_DIRETO, "#f85149");
        CategoriaFinanceira catFrete = obterOuCriarCategoria("Frete & Logística", TipoCategoriaFinanceira.CUSTO_LOGISTICO, "#d29922");

        BigDecimal custoTotalItens = lote.getItens().stream()
                .map(i -> i.getPrecoNotaUnitario().multiply(BigDecimal.valueOf(i.getQuantidadeAdquirida())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalPecas = lote.getItens().stream()
                .mapToInt(LoteBlankItem::getQuantidadeAdquirida)
                .sum();

        // 1. Transação Financeira: Custo dos Blanks
        if (custoTotalItens.compareTo(BigDecimal.ZERO) > 0) {
            TransacaoFinanceira txInsumo = new TransacaoFinanceira();
            txInsumo.setCodigoTransacao(gerarCodigoTransacao());
            txInsumo.setTipoMovimento(TipoMovimento.SAIDA);
            txInsumo.setTipoEvento(TipoEventoFinanceiro.COMPRA_BLANKS);
            txInsumo.setDescricao("Compra de Blanks: Lote #" + lote.getId() + " (" + totalPecas + " peças)");
            txInsumo.setValor(custoTotalItens);
            txInsumo.setDataCompetencia(lote.getDataEntrada() != null ? lote.getDataEntrada() : LocalDateTime.now());
            txInsumo.setDataLiquidacao(LocalDateTime.now());
            txInsumo.setStatus(StatusTransacao.REALIZADO);
            txInsumo.setModuloOrigem(ModuloOrigem.INVENTORY);
            txInsumo.setOrigemId(lote.getId());
            txInsumo.setContaFinanceira(conta);
            txInsumo.setCategoriaFinanceira(catInsumo);

            salvarTransacaoEAtualizarSaldo(txInsumo, conta);
        }

        // 2. Transação Financeira: Frete dos Blanks
        if (lote.getValorFreteTotal() != null && lote.getValorFreteTotal().compareTo(BigDecimal.ZERO) > 0) {
            TransacaoFinanceira txFrete = new TransacaoFinanceira();
            txFrete.setCodigoTransacao(gerarCodigoTransacao());
            txFrete.setTipoMovimento(TipoMovimento.SAIDA);
            txFrete.setTipoEvento(TipoEventoFinanceiro.FRETE_BLANKS);
            txFrete.setDescricao("Rateio de Frete: Lote #" + lote.getId());
            txFrete.setValor(lote.getValorFreteTotal());
            txFrete.setDataCompetencia(lote.getDataEntrada() != null ? lote.getDataEntrada() : LocalDateTime.now());
            txFrete.setDataLiquidacao(LocalDateTime.now());
            txFrete.setStatus(StatusTransacao.REALIZADO);
            txFrete.setModuloOrigem(ModuloOrigem.INVENTORY);
            txFrete.setOrigemId(lote.getId());
            txFrete.setContaFinanceira(conta);
            txFrete.setCategoriaFinanceira(catFrete);

            salvarTransacaoEAtualizarSaldo(txFrete, conta);
        }

        // 3. Vetor Físico: Ingestão de Lote
        BigDecimal custoTotalLote = custoTotalItens.add(lote.getValorFreteTotal() != null ? lote.getValorFreteTotal() : BigDecimal.ZERO);
        BigDecimal custoUnitarioMedio = totalPecas > 0
                ? custoTotalLote.divide(BigDecimal.valueOf(totalPecas), 6, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        LedgerEstoqueMovimento evFisico = new LedgerEstoqueMovimento();
        evFisico.setCodigoEvento(gerarCodigoEvento());
        evFisico.setTipoOperacao(TipoOperacaoEstoque.INGESTAO_LOTE_BLANK);
        evFisico.setTipoInsumo(TipoInsumo.BLANK);
        evFisico.setVariacaoVolumetrica("+" + totalPecas + " un");
        evFisico.setQuantidadeDelta(BigDecimal.valueOf(totalPecas));
        evFisico.setImpactoDetalhado("Ingestão de " + lote.getItens().size() + " grade(s) com rateio de frete");
        evFisico.setReferenciaMatriz("Lote Blanks #" + lote.getId());
        evFisico.setCustoUnitarioAplicado(custoUnitarioMedio);
        evFisico.setCustoTotalImpactado(custoTotalLote);
        evFisico.setDataHora(lote.getDataEntrada() != null ? lote.getDataEntrada() : LocalDateTime.now());

        ledgerFisicoRepository.save(evFisico);
    }

    @Transactional
    public void registrarEntradaDtf(EntradaDtf entrada) {
        if (entrada == null) {
            return;
        }

        ContaFinanceira conta = obterContaPadrao();
        CategoriaFinanceira catDtf = obterOuCriarCategoria("Matéria-Prima (DTF)", TipoCategoriaFinanceira.CUSTO_DIRETO, "#a371f7");
        CategoriaFinanceira catFrete = obterOuCriarCategoria("Frete & Logística", TipoCategoriaFinanceira.CUSTO_LOGISTICO, "#d29922");

        // 1. Transação Financeira: Valor da Nota do DTF
        if (entrada.getValorNota() != null && entrada.getValorNota().compareTo(BigDecimal.ZERO) > 0) {
            TransacaoFinanceira txNota = new TransacaoFinanceira();
            txNota.setCodigoTransacao(gerarCodigoTransacao());
            txNota.setTipoMovimento(TipoMovimento.SAIDA);
            txNota.setTipoEvento(TipoEventoFinanceiro.COMPRA_DTF);
            txNota.setDescricao("Compra DTF: Rolo " + entrada.getLarguraCm() + "x" + entrada.getComprimentoCm() + "cm (#" + entrada.getId() + ")");
            txNota.setValor(entrada.getValorNota());
            txNota.setDataCompetencia(entrada.getRegistradoEm() != null ? entrada.getRegistradoEm() : LocalDateTime.now());
            txNota.setDataLiquidacao(LocalDateTime.now());
            txNota.setStatus(StatusTransacao.REALIZADO);
            txNota.setModuloOrigem(ModuloOrigem.FINANCE_DTF);
            txNota.setOrigemId(entrada.getId());
            txNota.setContaFinanceira(conta);
            txNota.setCategoriaFinanceira(catDtf);

            salvarTransacaoEAtualizarSaldo(txNota, conta);
        }

        // 2. Transação Financeira: Custo Logístico do DTF
        if (entrada.getCustoLogistico() != null && entrada.getCustoLogistico().compareTo(BigDecimal.ZERO) > 0) {
            TransacaoFinanceira txFrete = new TransacaoFinanceira();
            txFrete.setCodigoTransacao(gerarCodigoTransacao());
            txFrete.setTipoMovimento(TipoMovimento.SAIDA);
            txFrete.setTipoEvento(TipoEventoFinanceiro.FRETE_DTF);
            txFrete.setDescricao("Frete Logístico DTF: Entrada #" + entrada.getId());
            txFrete.setValor(entrada.getCustoLogistico());
            txFrete.setDataCompetencia(entrada.getRegistradoEm() != null ? entrada.getRegistradoEm() : LocalDateTime.now());
            txFrete.setDataLiquidacao(LocalDateTime.now());
            txFrete.setStatus(StatusTransacao.REALIZADO);
            txFrete.setModuloOrigem(ModuloOrigem.FINANCE_DTF);
            txFrete.setOrigemId(entrada.getId());
            txFrete.setContaFinanceira(conta);
            txFrete.setCategoriaFinanceira(catFrete);

            salvarTransacaoEAtualizarSaldo(txFrete, conta);
        }

        // 3. Vetor Físico: Ingestão de Lote DTF
        LedgerEstoqueMovimento evFisico = new LedgerEstoqueMovimento();
        evFisico.setCodigoEvento(gerarCodigoEvento());
        evFisico.setTipoOperacao(TipoOperacaoEstoque.INGESTAO_LOTE_DTF);
        evFisico.setTipoInsumo(TipoInsumo.DTF);
        evFisico.setVariacaoVolumetrica("+" + entrada.getAreaLoteCm2().setScale(0, RoundingMode.HALF_UP) + " cm²");
        evFisico.setQuantidadeDelta(entrada.getAreaLoteCm2());
        evFisico.setImpactoDetalhado("Novo Custo Médio Ponderado: R$ " + entrada.getPrecoMedioPosteriorCm2() + "/cm²");
        evFisico.setReferenciaMatriz("Rolo DTF " + entrada.getLarguraCm() + "x" + entrada.getComprimentoCm() + "cm");
        evFisico.setCustoUnitarioAplicado(entrada.getPrecoMedioPosteriorCm2());
        evFisico.setCustoTotalImpactado(entrada.getValorTotalGasto());
        evFisico.setDataHora(entrada.getRegistradoEm() != null ? entrada.getRegistradoEm() : LocalDateTime.now());

        ledgerFisicoRepository.save(evFisico);
    }

    @Transactional
    public void registrarOrdemProducao(
            OrdemProducao ordem,
            BigDecimal areaNominalUtilCm2,
            BigDecimal areaRealTotalCm2,
            BigDecimal custoMedioDtf
    ) {
        if (ordem == null) {
            return;
        }

        LocalDateTime dataHora = ordem.getRegistradaEm() != null ? ordem.getRegistradaEm() : LocalDateTime.now();

        // 1. Vetor Físico: Baixa dos Blanks
        LedgerEstoqueMovimento evBlanks = new LedgerEstoqueMovimento();
        evBlanks.setCodigoEvento(gerarCodigoEvento());
        evBlanks.setTipoOperacao(TipoOperacaoEstoque.BAIXA_MANUFATURA_BLANK);
        evBlanks.setTipoInsumo(TipoInsumo.BLANK);
        evBlanks.setVariacaoVolumetrica("-" + ordem.getQuantidadeProduzir() + " un");
        evBlanks.setQuantidadeDelta(BigDecimal.valueOf(ordem.getQuantidadeProduzir()).negate());
        evBlanks.setImpactoDetalhado("Consumo FIFO de blanks para " + ordem.getDescricaoModelo());
        evBlanks.setReferenciaMatriz("Ordem Produção #" + ordem.getId());
        BigDecimal custoUnitarioBlank = ordem.getQuantidadeProduzir() > 0
                ? ordem.getCustoBlanksTotal().divide(BigDecimal.valueOf(ordem.getQuantidadeProduzir()), 6, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        evBlanks.setCustoUnitarioAplicado(custoUnitarioBlank);
        evBlanks.setCustoTotalImpactado(ordem.getCustoBlanksTotal());
        evBlanks.setDataHora(dataHora);
        ledgerFisicoRepository.save(evBlanks);

        // 2. Vetor Físico: Baixa da Área Útil de DTF
        BigDecimal areaNominalTotal = areaNominalUtilCm2.multiply(BigDecimal.valueOf(ordem.getQuantidadeProduzir()));
        BigDecimal custoAreaUtil = areaNominalTotal.multiply(custoMedioDtf).setScale(2, RoundingMode.HALF_UP);

        LedgerEstoqueMovimento evDtfUtil = new LedgerEstoqueMovimento();
        evDtfUtil.setCodigoEvento(gerarCodigoEvento());
        evDtfUtil.setTipoOperacao(TipoOperacaoEstoque.BAIXA_MANUFATURA_DTF);
        evDtfUtil.setTipoInsumo(TipoInsumo.DTF);
        evDtfUtil.setVariacaoVolumetrica("-" + areaNominalTotal.setScale(0, RoundingMode.HALF_UP) + " cm²");
        evDtfUtil.setQuantidadeDelta(areaNominalTotal.negate());
        evDtfUtil.setImpactoDetalhado("Área impressa útil aplicada nas peças");
        evDtfUtil.setReferenciaMatriz("Ordem Produção #" + ordem.getId());
        evDtfUtil.setCustoUnitarioAplicado(custoMedioDtf);
        evDtfUtil.setCustoTotalImpactado(custoAreaUtil);
        evDtfUtil.setDataHora(dataHora);
        ledgerFisicoRepository.save(evDtfUtil);

        // 3. Vetor Físico: Refugo / Desperdício Financeiro de Corte
        BigDecimal areaPerda = areaRealTotalCm2.subtract(areaNominalTotal);
        if (areaPerda.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal custoRefugo = areaPerda.multiply(custoMedioDtf).setScale(2, RoundingMode.HALF_UP);

            LedgerEstoqueMovimento evRefugo = new LedgerEstoqueMovimento();
            evRefugo.setCodigoEvento(gerarCodigoEvento());
            evRefugo.setTipoOperacao(TipoOperacaoEstoque.REFUGO_DTF);
            evRefugo.setTipoInsumo(TipoInsumo.DTF);
            evRefugo.setVariacaoVolumetrica("-" + areaPerda.setScale(0, RoundingMode.HALF_UP) + " cm²");
            evRefugo.setQuantidadeDelta(areaPerda.negate());
            evRefugo.setImpactoDetalhado("Capital consumido por perda de corte (" + (100 - ordem.getFatorAproveitamentoDtfPerc().doubleValue()) + "%)");
            evRefugo.setReferenciaMatriz("Ordem Produção #" + ordem.getId());
            evRefugo.setCustoUnitarioAplicado(custoMedioDtf);
            evRefugo.setCustoTotalImpactado(custoRefugo);
            evRefugo.setDataHora(dataHora);
            ledgerFisicoRepository.save(evRefugo);
        }

        // 4. Transação Financeira: Custo Operacional (se houver)
        if (ordem.getCustoOperacionalTotal() != null && ordem.getCustoOperacionalTotal().compareTo(BigDecimal.ZERO) > 0) {
            ContaFinanceira conta = obterContaPadrao();
            CategoriaFinanceira catOp = obterOuCriarCategoria("Custos Operacionais", TipoCategoriaFinanceira.CUSTO_OPERACIONAL, "#db61a2");

            TransacaoFinanceira txOp = new TransacaoFinanceira();
            txOp.setCodigoTransacao(gerarCodigoTransacao());
            txOp.setTipoMovimento(TipoMovimento.SAIDA);
            txOp.setTipoEvento(TipoEventoFinanceiro.CUSTO_OPERACIONAL);
            txOp.setDescricao("Custo Operacional de Produção: OP #" + ordem.getId());
            txOp.setValor(ordem.getCustoOperacionalTotal());
            txOp.setDataCompetencia(dataHora);
            txOp.setDataLiquidacao(LocalDateTime.now());
            txOp.setStatus(StatusTransacao.REALIZADO);
            txOp.setModuloOrigem(ModuloOrigem.PRODUCTION);
            txOp.setOrigemId(ordem.getId());
            txOp.setContaFinanceira(conta);
            txOp.setCategoriaFinanceira(catOp);

            salvarTransacaoEAtualizarSaldo(txOp, conta);
        }
    }

    @Transactional
    public TransacaoFinanceira registrarTransacaoManual(LancamentoManualRequest request) {
        ContaFinanceira conta = request.getContaId() != null
                ? contaRepository.findById(request.getContaId()).orElseGet(this::obterContaPadrao)
                : obterContaPadrao();

        CategoriaFinanceira categoria;
        if (request.getCategoriaId() != null) {
            categoria = categoriaRepository.findById(request.getCategoriaId()).orElseGet(() -> obterCategoriaPorEvento(request.getTipoEvento()));
        } else {
            categoria = obterCategoriaPorEvento(request.getTipoEvento());
        }

        TransacaoFinanceira tx = new TransacaoFinanceira();
        tx.setCodigoTransacao(gerarCodigoTransacao());
        tx.setTipoMovimento(request.getTipoMovimento());
        tx.setTipoEvento(request.getTipoEvento());
        tx.setDescricao(request.getDescricao());
        tx.setValor(request.getValor());
        tx.setDataCompetencia(LocalDateTime.now());
        tx.setDataLiquidacao(LocalDateTime.now());
        tx.setStatus(StatusTransacao.REALIZADO);
        tx.setModuloOrigem(ModuloOrigem.MANUAL);
        tx.setContaFinanceira(conta);
        tx.setCategoriaFinanceira(categoria);
        tx.setObservacoes(request.getObservacoes());

        // Se for receita com CPV informado, calcula a margem nominal
        if (request.getTipoMovimento() == TipoMovimento.ENTRADA && request.getCpvHistorico() != null) {
            tx.setCpvHistorico(request.getCpvHistorico());
            BigDecimal margem = request.getValor().subtract(request.getCpvHistorico());
            tx.setMargemNominal(margem);
        }

        salvarTransacaoEAtualizarSaldo(tx, conta);
        return tx;
    }

    public List<TransacaoFinanceira> listarTransacoesRecentes() {
        return transacaoRepository.findTop100ByOrderByDataCompetenciaDescIdDesc();
    }

    public List<LedgerEstoqueMovimento> listarMovimentacoesFisicasRecentes() {
        return ledgerFisicoRepository.findTop100ByOrderByDataHoraDescIdDesc();
    }

    public List<ContaFinanceira> listarContas() {
        return contaRepository.findByAtivoTrue();
    }

    public List<CategoriaFinanceira> listarCategorias() {
        return categoriaRepository.findAll();
    }

    public KpisFluxoCaixa obterKpis() {
        BigDecimal totalReceitas = transacaoRepository.somarPorTipoMovimentoEStatus(TipoMovimento.ENTRADA, StatusTransacao.REALIZADO);
        BigDecimal totalDespesas = transacaoRepository.somarPorTipoMovimentoEStatus(TipoMovimento.SAIDA, StatusTransacao.REALIZADO);
        BigDecimal margemNominalTotal = transacaoRepository.somarMargemNominalPorStatus(StatusTransacao.REALIZADO);

        // Se não houver margem nominal calculada explícita de vendas, a margem de contribuição é Receitas - Despesas de Insumos
        BigDecimal margemContribuicao = margemNominalTotal.compareTo(BigDecimal.ZERO) > 0
                ? margemNominalTotal
                : totalReceitas.subtract(totalDespesas);

        BigDecimal refugoFinanceiro = ledgerFisicoRepository.somarCustoPorTipoOperacao(TipoOperacaoEstoque.REFUGO_DTF);

        EstoqueFluidoDtf estoque = estoqueFluidoDtfRepository.findTopByOrderByIdDesc().orElse(null);
        BigDecimal saldoDtfCm2 = estoque != null && estoque.getAreaTotalCm2() != null ? estoque.getAreaTotalCm2() : BigDecimal.ZERO;
        BigDecimal precoMedioDtf = estoque != null && estoque.getPrecoMedioPorCm2() != null ? estoque.getPrecoMedioPorCm2() : BigDecimal.ZERO;

        BigDecimal saldoConsolidadoCaixa = totalReceitas.subtract(totalDespesas);

        return new KpisFluxoCaixa(
                margemContribuicao,
                refugoFinanceiro,
                saldoDtfCm2,
                precoMedioDtf,
                saldoConsolidadoCaixa,
                totalReceitas,
                totalDespesas
        );
    }

    private void salvarTransacaoEAtualizarSaldo(TransacaoFinanceira tx, ContaFinanceira conta) {
        if (tx.getStatus() == StatusTransacao.REALIZADO) {
            BigDecimal saldoAtual = conta.getSaldoAtual() != null ? conta.getSaldoAtual() : BigDecimal.ZERO;
            if (tx.getTipoMovimento() == TipoMovimento.ENTRADA) {
                conta.setSaldoAtual(saldoAtual.add(tx.getValor()));
            } else {
                conta.setSaldoAtual(saldoAtual.subtract(tx.getValor()));
            }
            contaRepository.save(conta);
        }
        transacaoRepository.save(tx);
    }

    private ContaFinanceira obterContaPadrao() {
        return contaRepository.findFirstByAtivoTrueOrderByIdAsc().orElseGet(() -> {
            ContaFinanceira nova = new ContaFinanceira("Caixa Operacional Principal", TipoConta.CAIXA_INTERNO, BigDecimal.ZERO);
            return contaRepository.save(nova);
        });
    }

    private CategoriaFinanceira obterOuCriarCategoria(String nome, TipoCategoriaFinanceira tipo, String corHex) {
        return categoriaRepository.findFirstByNomeIgnoreCase(nome).orElseGet(() -> {
            CategoriaFinanceira cat = new CategoriaFinanceira(nome, tipo, corHex);
            return categoriaRepository.save(cat);
        });
    }

    private CategoriaFinanceira obterCategoriaPorEvento(TipoEventoFinanceiro evento) {
        return switch (evento) {
            case COMPRA_BLANKS -> obterOuCriarCategoria("Matéria-Prima (Blanks)", TipoCategoriaFinanceira.CUSTO_DIRETO, "#f85149");
            case FRETE_BLANKS, FRETE_DTF -> obterOuCriarCategoria("Frete & Logística", TipoCategoriaFinanceira.CUSTO_LOGISTICO, "#d29922");
            case COMPRA_DTF -> obterOuCriarCategoria("Matéria-Prima (DTF)", TipoCategoriaFinanceira.CUSTO_DIRETO, "#a371f7");
            case VENDA_PRODUTO -> obterOuCriarCategoria("Receita de Vendas", TipoCategoriaFinanceira.RECEITA, "#2ea043");
            case CUSTO_OPERACIONAL -> obterOuCriarCategoria("Custos Operacionais", TipoCategoriaFinanceira.CUSTO_OPERACIONAL, "#db61a2");
            case DESPESA_FIXA -> obterOuCriarCategoria("Despesas Fixas", TipoCategoriaFinanceira.DESPESA_FIXA, "#f0883e");
            case APORTE -> obterOuCriarCategoria("Aporte de Capital", TipoCategoriaFinanceira.APORTE, "#58a6ff");
            case RETIRADA, DESPESA_MANUAL -> obterOuCriarCategoria("Outras Despesas", TipoCategoriaFinanceira.DESPESA_FIXA, "#8b949e");
        };
    }

    private String gerarCodigoTransacao() {
        return "TX-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    private String gerarCodigoEvento() {
        return "EV-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    public record KpisFluxoCaixa(
            BigDecimal margemContribuicaoLiquida,
            BigDecimal refugoFinanceiroTotal,
            BigDecimal saldoDtfCm2,
            BigDecimal precoMedioDtfPorCm2,
            BigDecimal saldoConsolidadoCaixa,
            BigDecimal totalReceitas,
            BigDecimal totalDespesas
    ) {}
}
