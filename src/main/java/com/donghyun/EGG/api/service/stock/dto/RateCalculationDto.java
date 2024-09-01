package com.donghyun.EGG.api.service.stock.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class RateCalculationDto {
    private String startDate;
    private String endDate;
    private int ndxRate;
    private int spxRate;
    private int djiRate;

    @Builder
    public RateCalculationDto(String startDate, String endDate, int ndxRate, int spxRate, int djiRate) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.ndxRate = ndxRate;
        this.spxRate = spxRate;
        this.djiRate = djiRate;
    }
}
