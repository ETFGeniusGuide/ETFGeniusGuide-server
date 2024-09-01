package com.donghyun.EGG.api.controller.stock.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class RoiResponse {
    List<RateCalculationResponse> ndxRoiList;
    List<RateCalculationResponse> spxRoiList;
    List<RateCalculationResponse> djiRoiList;

    @Builder
    public RoiResponse(List<RateCalculationResponse> ndxRoiList, List<RateCalculationResponse> spxRoiList, List<RateCalculationResponse> djiRoiList) {
        this.ndxRoiList = ndxRoiList;
        this.spxRoiList = spxRoiList;
        this.djiRoiList = djiRoiList;
    }
}
