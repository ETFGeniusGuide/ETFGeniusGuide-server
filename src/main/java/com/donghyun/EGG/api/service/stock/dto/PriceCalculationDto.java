package com.donghyun.EGG.api.service.stock.dto;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class PriceCalculationDto {
    String localDate;
    int price;

    @Builder

    public PriceCalculationDto(String localDate, int price) {
        this.localDate = localDate;
        this.price = price;
    }
}
