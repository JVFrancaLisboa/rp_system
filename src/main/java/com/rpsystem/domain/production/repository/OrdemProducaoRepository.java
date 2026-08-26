package com.rpsystem.domain.production.repository;

import com.rpsystem.domain.production.model.OrdemProducao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrdemProducaoRepository extends JpaRepository<OrdemProducao, Long> {

	Optional<OrdemProducao> findTopByOrderByRegistradaEmDescIdDesc();

	List<OrdemProducao> findTop12ByOrderByRegistradaEmDescIdDesc();
}