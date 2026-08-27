package com.rpsystem.domain.production.repository;

import com.rpsystem.domain.production.model.OrdemProducao;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OrdemProducaoRepository extends JpaRepository<OrdemProducao, Long> {

	Optional<OrdemProducao> findTopByOrderByRegistradaEmDescIdDesc();

	List<OrdemProducao> findTop12ByOrderByRegistradaEmDescIdDesc();

	List<OrdemProducao> findByQuantidadeDisponivelGreaterThanOrderByRegistradaEmDesc(int minQuantidade);

	@Query("SELECT COALESCE(SUM(op.quantidadeDisponivel), 0) FROM OrdemProducao op")
	long somarQuantidadeDisponivel();
}