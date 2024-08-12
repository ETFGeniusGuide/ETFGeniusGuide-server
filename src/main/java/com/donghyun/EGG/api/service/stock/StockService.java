package com.donghyun.EGG.api.service.stock;

import com.donghyun.EGG.api.controller.stock.response.StockInfoResponse;
import com.donghyun.EGG.api.service.stock.dto.StockDto;
import com.donghyun.EGG.domain.stockprice.TigerETFMonthlyPrice;
import com.donghyun.EGG.domain.stockprice.repository.TigerETFMonthlyPriceRepository;
import com.donghyun.EGG.security.KisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockService {

    private final KisUtil kisUtil;
    private final TigerETFMonthlyPriceRepository tigerETFMonthlyPriceRepository;

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

    public String saveStock(StockDto stockDto) throws JSONException, IOException {
        JSONObject result = new JSONObject(kisUtil.loadStockMonthlyPrice(stockDto));

        String name = result.getJSONObject("output1").getString("hts_kor_isnm");
        log.debug("[StockService][saveStock] ETF명: {}", name);

        JSONArray jsonArray = new JSONArray(result.getString("output2"));

        for(int i = 0; i < jsonArray.length(); i++) {
            JSONObject object = jsonArray.getJSONObject(i);

            String s_date = object.getString("stck_bsop_date");
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            LocalDate localDate = LocalDate.parse(s_date, dateTimeFormatter);

            int price = Integer.parseInt(object.getString("stck_clpr"));

            tigerETFMonthlyPriceRepository.save(TigerETFMonthlyPrice.builder()
                    .name(name)
                    .date(localDate)
                    .price(price)
                    .build());
        }

        return name;
    }
}
