package com.rpsystem.domain.inventory.repository;

import com.rpsystem.domain.inventory.model.LoteBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LoteBlankRepository extends JpaRepository<LoteBlank, Long> {

	@Query("select count(lote) from LoteBlank lote")
	long totalLotes();
}