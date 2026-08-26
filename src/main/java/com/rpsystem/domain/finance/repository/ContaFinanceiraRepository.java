package com.rpsystem.domain.finance.repository;

import com.rpsystem.domain.finance.model.ContaFinanceira;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContaFinanceiraRepository extends JpaRepository<ContaFinanceira, Long> {

    List<ContaFinanceira> findByAtivoTrue();

    Optional<ContaFinanceira> findFirstByAtivoTrueOrderByIdAsc();
}
