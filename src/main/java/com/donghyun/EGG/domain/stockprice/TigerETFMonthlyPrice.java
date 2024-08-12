package com.donghyun.EGG.domain.stockprice;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

@Entity
@Getter
@RequiredArgsConstructor
@Slf4j
@Table(name = "tiger_ETF_monthly_price")
public class TigerETFMonthlyPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private int price;

    private LocalDate date;


    @Builder
    public TigerETFMonthlyPrice(String name, int price, LocalDate date) {
        this.name = name;
        this.price = price;
        this.date = date;
    }
}
