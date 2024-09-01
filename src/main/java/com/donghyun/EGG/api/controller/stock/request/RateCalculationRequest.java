package com.donghyun.EGG.api.controller.stock.request;

import com.donghyun.EGG.api.service.stock.dto.RateCalculationDto;
import lombok.Data;

@Data
public class RateCalculationRequest {
    private String startDate;
    private String endDate;
    private Integer ndxRate;
    private Integer spxRate;
    private Integer djiRate;


    public RateCalculationDto toInvestRateDto() {
        return RateCalculationDto.builder()
                .startDate(this.startDate)
                .endDate(this.endDate)
                .ndxRate(this.ndxRate)
                .spxRate(this.spxRate)
                .djiRate(this.djiRate)
                .build();
    }
}
