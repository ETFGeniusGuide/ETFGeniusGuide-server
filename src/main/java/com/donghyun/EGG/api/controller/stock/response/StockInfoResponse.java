package com.donghyun.EGG.api.controller.stock.response;

import lombok.Builder;
import lombok.Data;

@Data
public class StockInfoResponse {
    private String stockName;
    private int stockPrice;

    @Builder
    public StockInfoResponse(String stockName, int stockPrice) {
        this.stockName = stockName;
        this.stockPrice = stockPrice;
    }
}
