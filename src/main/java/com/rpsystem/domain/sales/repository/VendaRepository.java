package com.rpsystem.domain.sales.repository;

import com.rpsystem.domain.sales.model.Venda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface VendaRepository extends JpaRepository<Venda, Long> {

    List<Venda> findTop50ByOrderByDataVendaDescIdDesc();

    @Query("SELECT COALESCE(SUM(v.valorTotal), 0) FROM Venda v")
    BigDecimal somarFaturamentoTotal();

    @Query("SELECT COALESCE(SUM(v.lucroBruto), 0) FROM Venda v")
    BigDecimal somarLucroBrutoTotal();

    @Query("SELECT COALESCE(SUM(v.quantidade), 0) FROM Venda v")
    long somarTotalPecasVendidas();
}
