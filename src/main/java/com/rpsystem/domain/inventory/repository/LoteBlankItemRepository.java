package com.rpsystem.domain.inventory.repository;

import com.rpsystem.domain.inventory.model.LoteBlankItem;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LoteBlankItemRepository extends JpaRepository<LoteBlankItem, Long> {

    @Query("select coalesce(sum(item.quantidadeDisponivel), 0) from LoteBlankItem item")
    long somaQuantidadeDisponivel();

    @Query("""
        select item
        from LoteBlankItem item
        join item.loteBlank lote
        where item.quantidadeDisponivel > 0
        order by lote.dataEntrada asc, item.id asc
        """)
    List<LoteBlankItem> findAllDisponiveisOrderByEntradaAsc();

    @Query("""
        select item
        from LoteBlankItem item
        join item.loteBlank lote
        where item.skuBase = :skuBase
          and item.tamanho = :tamanho
          and item.quantidadeDisponivel > :quantidadeDisponivel
        order by lote.dataEntrada asc, item.id asc
        """)
    List<LoteBlankItem> findDisponiveisParaSku(
        @Param("skuBase") String skuBase,
        @Param("tamanho") String tamanho,
        @Param("quantidadeDisponivel") Integer quantidadeDisponivel
    );
}