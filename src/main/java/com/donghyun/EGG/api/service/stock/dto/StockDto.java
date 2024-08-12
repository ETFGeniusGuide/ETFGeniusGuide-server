package com.donghyun.EGG.api.service.stock.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StockDto {
    private String accessToken;
    private String pdno;

    @Builder
    public StockDto(String accessToken, String pdno) {
        this.accessToken = accessToken;
        this.pdno = pdno;
    }
}
