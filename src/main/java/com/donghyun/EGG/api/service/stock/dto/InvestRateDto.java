package com.donghyun.EGG.api.service.stock.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class InvestRateDto {
    private int ndxRate;
    private int spxRate;
    private int djiRate;

    @Builder
    public InvestRateDto(int ndxRate, int spxRate, int djiRate) {
        this.ndxRate = ndxRate;
        this.spxRate = spxRate;
        this.djiRate = djiRate;
    }
}
