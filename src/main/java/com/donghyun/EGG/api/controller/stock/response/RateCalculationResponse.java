package com.donghyun.EGG.api.controller.stock.response;

import lombok.Builder;
import lombok.Data;

@Data
public class RateCalculationResponse {
    private String date;
    private double roi;

    @Builder
    public RateCalculationResponse(String date, double roi) {
        this.date = date;
        this.roi = roi;
    }
}
