package com.rpsystem.domain.finance.repository;

import com.rpsystem.domain.finance.model.CategoriaFinanceira;
import com.rpsystem.domain.finance.model.TipoCategoriaFinanceira;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoriaFinanceiraRepository extends JpaRepository<CategoriaFinanceira, Long> {

    List<CategoriaFinanceira> findByTipo(TipoCategoriaFinanceira tipo);

    Optional<CategoriaFinanceira> findFirstByNomeIgnoreCase(String nome);
}
