package com.rpsystem.domain.finance.repository;

import com.rpsystem.domain.finance.model.EstoqueFluidoDtf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface EstoqueFluidoDtfRepository extends JpaRepository<EstoqueFluidoDtf, Long> {

    Optional<EstoqueFluidoDtf> findTopByOrderByIdDesc();

    @Query("select count(estoque) from EstoqueFluidoDtf estoque")
    long totalEntradas();
}