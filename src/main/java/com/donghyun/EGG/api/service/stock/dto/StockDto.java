package com.donghyun.EGG.api.service.stock.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StockDto {
    private String access_token;
    private String pdno;

    @Builder
    public StockDto(String access_token, String pdno) {
        this.access_token = access_token;
        this.pdno = pdno;
    }
}
