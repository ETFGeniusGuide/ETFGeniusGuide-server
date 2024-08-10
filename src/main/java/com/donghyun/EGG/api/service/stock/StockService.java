package com.donghyun.EGG.api.service.stock;

import com.donghyun.EGG.api.controller.stock.response.StockInfoResponse;
import com.donghyun.EGG.api.service.stock.dto.StockDto;
import com.donghyun.EGG.security.KisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockService {

    private final KisUtil kisUtil;

    public StockInfoResponse loadStock(StockDto stockDto) throws IOException, JSONException {

        String result = kisUtil.loadStockInfo(stockDto);
        log.debug(result);

        JSONObject jsonObject = new JSONObject(result);

        // TODO: 2024-08-10 (010) Service에서 필요한 데이터 추출 후 정제
        String prdt_abrv_name = jsonObject.getString("prdt_abrv_name");
        log.debug("[StockService][loadStock] 종목이름: {}", prdt_abrv_name);

        int thdt_clpr = Integer.parseInt(jsonObject.getString("thdt_clpr"));
        log.debug("[StockService][loadStock] 종목 당일종가: {}", thdt_clpr);

        StockInfoResponse stockInfoResponse = StockInfoResponse.builder()
                .stockName(prdt_abrv_name)
                .stockPrice(thdt_clpr)
                .build();

        return stockInfoResponse;
    }
}
