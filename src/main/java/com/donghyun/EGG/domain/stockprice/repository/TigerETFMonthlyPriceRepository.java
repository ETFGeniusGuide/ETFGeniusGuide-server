package com.donghyun.EGG.domain.stockprice.repository;

import com.donghyun.EGG.domain.stockprice.TigerETFMonthlyPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TigerETFMonthlyPriceRepository extends JpaRepository<TigerETFMonthlyPrice, Long> {
}
