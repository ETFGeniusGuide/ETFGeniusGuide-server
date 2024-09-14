package com.donghyun.EGG.api.service.stock;

import com.donghyun.EGG.api.controller.stock.response.RateCalculationResponse;
import com.donghyun.EGG.api.controller.stock.response.StockInfoResponse;
import com.donghyun.EGG.api.service.stock.dto.PriceCalculationDto;
import com.donghyun.EGG.api.service.stock.dto.RateCalculationDto;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockService {

    private final KisUtil kisUtil;
    private final TigerETFMonthlyPriceRepository tigerETFMonthlyPriceRepository;

    /**
     * 종목 코드의 가격을 불러오는 서비스
     *
     * @param stockCode
     * @return
     * @throws IOException
     * @throws JSONException
     */
    public StockInfoResponse loadStock(String stockCode) throws IOException, JSONException {

        String result = kisUtil.loadStockInfo(stockCode);
        log.debug(result);

        JSONObject jsonObject = new JSONObject(result);

        // TODO: 2024-08-10 (010) Service에서 필요한 데이터 추출 후 정제
        String prdt_abrv_name = jsonObject.getString("prdt_abrv_name");
        log.debug("[loadStock] 종목이름: {}", prdt_abrv_name);

        int thdt_clpr = Integer.parseInt(jsonObject.getString("thdt_clpr"));
        log.debug("[loadStock] 종목 당일종가: {}", thdt_clpr);

        StockInfoResponse stockInfoResponse = StockInfoResponse.builder()
                .stockName(prdt_abrv_name)
                .stockPrice(thdt_clpr)
                .build();

        return stockInfoResponse;
    }

    /**
     * 종목코드를 받으면 6월 30일부터 현재까지의 국내지수추종 ETF 가격을 DB에 저장하는 서비스
     *
     * @param stockCode
     * @return
     * @throws JSONException
     * @throws IOException
     */
    public String saveStock(String stockCode) throws JSONException, IOException {
        JSONObject result = new JSONObject(kisUtil.loadStockMonthlyPrice(stockCode));

        String name = result.getJSONObject("output1").getString("hts_kor_isnm");
        log.debug("[saveStock] ETF명: {}", name);

        JSONArray jsonArray = new JSONArray(result.getString("output2"));

        for (int i = 0; i < jsonArray.length(); i++) {
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

    /**
     * 비율에 따른 월별 총 ETF 수익률 계산 서비스
     *
     * @param dto
     * @return
     */
    public List<RateCalculationResponse> calcInvestRate(RateCalculationDto dto) {

        ArrayList<PriceCalculationDto> ndxMonthlyPriceList;
        ArrayList<PriceCalculationDto> spxMonthlyPriceList;
        ArrayList<PriceCalculationDto> djiMonthlyPriceList;

        // 시작 날짜의 가격 가져오기 -> 리스트로
        try {
            ndxMonthlyPriceList = kisUtil.loadIndexMonthlyPrice(dto.getStartDate(), dto.getEndDate(), "133690");
            spxMonthlyPriceList = kisUtil.loadIndexMonthlyPrice(dto.getStartDate(), dto.getEndDate(), "360750");
            djiMonthlyPriceList = kisUtil.loadIndexMonthlyPrice(dto.getStartDate(), dto.getEndDate(), "458730");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Collections.sort(ndxMonthlyPriceList, (Comparator.comparing(PriceCalculationDto::getLocalDate)));
        Collections.sort(spxMonthlyPriceList, (Comparator.comparing(PriceCalculationDto::getLocalDate)));
        Collections.sort(djiMonthlyPriceList, (Comparator.comparing(PriceCalculationDto::getLocalDate)));


//        for(PriceCalculationDto ndxDto: ndxMonthlyPriceList) {
//            log.debug("[calcInvestRate] 월별 결과값: {}", ndxDto.getLocalDate());
//            log.debug("[calcInvestRate] 월별 결과값: {}", ndxDto.getPrice());
//        }

        // 각 ETF의 수익률, 비율에 따른 ETF별 수익률 초기화 시키기


        int sum = dto.getNdxRate() + dto.getSpxRate() + dto.getDjiRate();

        String startDate = dto.getStartDate();

        ArrayList<RateCalculationResponse> ndxRoiList = calcRate(dto.getNdxRate(), startDate, ndxMonthlyPriceList, sum);
        ArrayList<RateCalculationResponse> spxRoiList = calcRate(dto.getSpxRate(), startDate, spxMonthlyPriceList, sum);
        ArrayList<RateCalculationResponse> djiRoiList = calcRate(dto.getDjiRate(), startDate, djiMonthlyPriceList, sum);

        ArrayList<RateCalculationResponse> roiList = new ArrayList<>();
        for (int i = 0; i < ndxRoiList.size(); i++) {
            double roi = ndxRoiList.get(i).getRoi() + spxRoiList.get(i).getRoi() + djiRoiList.get(i).getRoi();


//            log.debug("{} {} {}", ndxRoiList.get(i).getRoi(), spxRoiList.get(i).getRoi(), djiRoiList.get(i).getRoi());


            roi = Math.round(roi * 100) / 100.0;

            String date = ndxRoiList.get(i).getDate();

            roiList.add(RateCalculationResponse.builder()
                    .roi(roi)
                    .date(date)
                    .build());
        }
//        for (int i = 0; i < ndxRoiList.size(); i++) {
//            log.debug("[calcInvestRate] {} 기간의 ETF 비율: {}", roiList.get(i).getDate(), roiList.get(i).getRoi());
//        }
        // ?
        // -> 계산식: 이전 비율에 따른 ETF 수익률 *  (현재 ETF 수익률 / 전달 ETF 수익률)
        // 해당 달의 3개의 수익률을 더해 날짜 및 총 수익률 저장
        // 반복


        return roiList;
    }


    // 비즈니스 로직

    ArrayList<RateCalculationResponse> calcRate(int ETFRate, String startDate, ArrayList<PriceCalculationDto> ETFMonthlyPriceList, int sum) {

        ArrayList<RateCalculationResponse> ETFRoiList = new ArrayList<>();
        // TODO: 2024-08-31 (031) 날짜 저장 시 문자열 처리가 아닌 LocalDate관리
        double prevETFRate = ((double) ETFRate / sum) * 100;
        double nowETFRate;

        ETFRoiList.add(RateCalculationResponse.builder()
                .roi(prevETFRate)
                .date(startDate.substring(0, 6))
                .build());

        for (int i = 1; i < ETFMonthlyPriceList.size(); i++) {

            nowETFRate = (prevETFRate * ETFMonthlyPriceList.get(i).getPrice() / ETFMonthlyPriceList.get(i - 1).getPrice());
            // 계산식

            ETFRoiList.add(RateCalculationResponse.builder()
                    .roi(nowETFRate)
                    .date(ETFMonthlyPriceList.get(i).getLocalDate().substring(0, 6))
                    .build());

            prevETFRate = nowETFRate;
        }

//        for (int i = 0; i < ETFRoiList.size(); i++) {
//            log.debug("[calcInvestRate] {} 기간의 ETF 비율: {}", ETFRoiList.get(i).getDate(), ETFRoiList.get(i).getRoi());
//        }

        return ETFRoiList;
    }
}
