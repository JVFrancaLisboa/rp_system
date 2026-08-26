package com.rpsystem.domain.finance.repository;

import com.rpsystem.domain.finance.model.StatusTransacao;
import com.rpsystem.domain.finance.model.TipoMovimento;
import com.rpsystem.domain.finance.model.TransacaoFinanceira;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface TransacaoFinanceiraRepository extends JpaRepository<TransacaoFinanceira, Long> {

    List<TransacaoFinanceira> findAllByOrderByDataCompetenciaDescIdDesc();

    List<TransacaoFinanceira> findTop100ByOrderByDataCompetenciaDescIdDesc();

    @Query("SELECT COALESCE(SUM(t.valor), 0) FROM TransacaoFinanceira t WHERE t.tipoMovimento = :tipoMovimento AND t.status = :status")
    BigDecimal somarPorTipoMovimentoEStatus(
            @Param("tipoMovimento") TipoMovimento tipoMovimento,
            @Param("status") StatusTransacao status
    );

    @Query("SELECT COALESCE(SUM(t.margemNominal), 0) FROM TransacaoFinanceira t WHERE t.status = :status AND t.margemNominal IS NOT NULL")
    BigDecimal somarMargemNominalPorStatus(@Param("status") StatusTransacao status);

    @Query("SELECT COUNT(t) FROM TransacaoFinanceira t")
    long totalTransacoes();
}
