package com.rpsystem.domain.finance.repository;

import com.rpsystem.domain.finance.model.LedgerEstoqueMovimento;
import com.rpsystem.domain.finance.model.TipoOperacaoEstoque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface LedgerEstoqueMovimentoRepository extends JpaRepository<LedgerEstoqueMovimento, Long> {

    List<LedgerEstoqueMovimento> findAllByOrderByDataHoraDescIdDesc();

    List<LedgerEstoqueMovimento> findTop100ByOrderByDataHoraDescIdDesc();

    @Query("SELECT COALESCE(SUM(l.custoTotalImpactado), 0) FROM LedgerEstoqueMovimento l WHERE l.tipoOperacao = :tipoOperacao")
    BigDecimal somarCustoPorTipoOperacao(@Param("tipoOperacao") TipoOperacaoEstoque tipoOperacao);
}
