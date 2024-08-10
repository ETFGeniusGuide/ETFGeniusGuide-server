package com.donghyun.EGG.api.controller.stock;

import com.donghyun.EGG.api.controller.stock.response.StockInfoResponse;
import com.donghyun.EGG.api.service.stock.StockService;
import com.donghyun.EGG.api.service.stock.dto.StockDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/stock")
@RequiredArgsConstructor
@Slf4j
public class StockController {

    private final StockService stockService;

    @GetMapping
    public StockInfoResponse loadStock(@RequestParam("access_token") String access_token, @RequestParam("stockCode") String stockCode) throws JSONException, IOException {

        log.debug("[StockController][loadStock] 종목코드: {}", stockCode);

        StockDto stockDto = StockDto.builder()
                .access_token(access_token)
                .pdno(stockCode)
                .build();

        return stockService.loadStock(stockDto);
    }


}
