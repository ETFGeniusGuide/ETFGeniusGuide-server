package com.donghyun.EGG.api.controller.stock;

import com.donghyun.EGG.api.controller.stock.request.RateCalculationRequest;
import com.donghyun.EGG.api.controller.stock.response.StockInfoResponse;
import com.donghyun.EGG.api.service.stock.StockService;
import com.donghyun.EGG.api.service.stock.dto.StockDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/stock")
@RequiredArgsConstructor
@Slf4j
public class StockController {

    private final StockService stockService;

    @GetMapping
    public StockInfoResponse loadStock(@RequestParam("access_token") String accessToken, @RequestParam("stock_code") String stockCode) throws JSONException, IOException {

        log.debug("[StockController][loadStock] 종목코드: {}", stockCode);

        StockDto stockDto = StockDto.builder()
                .accessToken(accessToken)
                .pdno(stockCode)
                .build();

        return stockService.loadStock(stockDto);
    }

    @GetMapping("saveprice")
    public String saveStock(@RequestParam("access_token") String accessToken, @RequestParam("stock_code") String stockCode) throws JSONException, IOException {

        StockDto stockDto = StockDto.builder()
                .accessToken(accessToken)
                .pdno(stockCode)
                .build();

        return stockService.saveStock(stockDto);
    }

    @GetMapping("calc")
    public String calcInvestRate(@RequestParam("access_token") String accessToken, @RequestBody RateCalculationRequest request) {

        log.debug("[StockController][calcInvestRate] 나스닥100: {}", request.getNdxRate());
        log.debug("[StockController][calcInvestRate] S&P500: {}", request.getSpxRate());
        log.debug("[StockController][calcInvestRate] 미국배당다우존스: {}", request.getDjiRate());

        // TODO: 2024-08-14 (014) 날짜 유효성 검사

        return stockService.calcInvestRate(accessToken, request.toInvestRateDto());
    }


}
