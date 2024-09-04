package com.donghyun.EGG.security;

import com.donghyun.EGG.api.service.stock.dto.PriceCalculationDto;
import com.donghyun.EGG.api.service.stock.dto.StockDto;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@Slf4j
@Component
public class KisUtil {

    @Value("${PROD_APPKEY}")
    private String prodAppkey;

    @Value("${PROD_APPSECRET}")
    private String prodAppSecret;

    @Value("${PROD}")
    private String prod;

    private String kisToken;

//    @PostConstruct
    public void init() {
        try {
            this.kisToken = generateKisToken();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public String getKisToken() {
        return this.kisToken;
    }

    public String generateKisToken() throws IOException, JSONException {
        String apiURL = prod + "/oauth2/tokenP";

        BufferedReader br;
        // TODO: 2024-09-01 (001) WebClient로 변경
        try {
            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");

            JSONObject json = new JSONObject();
            json.put("grant_type", "client_credentials");
            json.put("appkey", prodAppkey); // prodAppkey 변수를 여기에 사용하세요
            json.put("appsecret", prodAppSecret); // prodAppSecret 변수를 여기에 사용하세요

            // OutputStream을 사용하여 JSON 데이터를 전송
            con.setDoOutput(true);
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = json.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

//            con.setRequestProperty("grant_type", "client_credentials");
//            con.setRequestProperty("appkey", prodAppkey);
//            con.setRequestProperty("appsecret", prodAppSecret);

            int responseCode = con.getResponseCode();
            if (responseCode == 200) { // 정상 호출
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));

            } else {  // 에러 발생
                log.debug("kis토큰 발급시 에러 발생!");
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                log.debug("에러: {}", br.readLine());
                return "에러발생!";
            }

        } catch (IOException e) {
            throw new IOException(e);
        }

        JSONObject tokenResult = new JSONObject(br.readLine());
        String accessToken = tokenResult.getString("access_token");
        log.debug("{}: access_token", accessToken);

        this.kisToken = accessToken;
//        log.debug("[KisUtill][generateKisToken] kisToken: {}", getKisToken());

        return accessToken;
    }

    public String loadStockInfo(String stockCode) throws IOException, JSONException {
        String apiUrl = prod + "/uapi/domestic-stock/v1/quotations/search-stock-info?PRDT_TYPE_CD=300";
        apiUrl += "&PDNO=" + stockCode;

        BufferedReader br;

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();


            con.setRequestProperty("authorization", "Bearer " + this.kisToken);
            con.setRequestProperty("appkey", prodAppkey);
            con.setRequestProperty("appsecret", prodAppSecret);
            con.setRequestProperty("tr_id", "CTPF1002R");
            con.setRequestProperty("custtype", "P");

            int responseCode = con.getResponseCode();

            if (responseCode == 200) {
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        JSONObject response = new JSONObject(br.readLine());
//        log.debug("[KisUtil][loadStockInfo] {}: 결과값", response.toString());


        String result = response.getString("output");

        // TODO: 2024-08-10 (010) String이 아닌 주식 정보를 담아서 보내줌. 데이터 정제는 Service에서
        return result;
    }

    public String loadStockMonthlyPrice(String stockCode) throws IOException, JSONException {
        String apiUrl = prod + "/uapi/domestic-stock/v1/quotations/inquire-daily-itemchartprice?fid_cond_mrkt_div_code=J&fid_input_date_1=20230601&fid_period_div_code=M&fid_org_adj_prc=1";
        apiUrl += "&fid_input_iscd=" + stockCode;
        apiUrl += "&fid_input_date_2=" + today();

        BufferedReader br;

        try {
            URL url = new URL(apiUrl);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestProperty("authorization", "Bearer " + this.kisToken);
            con.setRequestProperty("appkey", prodAppkey);
            con.setRequestProperty("appsecret", prodAppSecret);
            con.setRequestProperty("tr_id", "FHKST03010100");

            int responseCode = con.getResponseCode();

            if (responseCode == 200) {
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }


        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String result = br.readLine();

        return result;
    }

    public ArrayList<PriceCalculationDto> loadIndexMonthlyPrice(String startDate, String endDate, String pdno) throws IOException {
        String apiUrl = prod + "/uapi/domestic-stock/v1/quotations/inquire-daily-itemchartprice?fid_cond_mrkt_div_code=J&fid_period_div_code=M&fid_org_adj_prc=1";

        apiUrl += "&fid_input_iscd=" + pdno;
        apiUrl += "&fid_input_date_1=" + startDate;
        apiUrl += "&fid_input_date_2=" + endDate;

        BufferedReader br;

        try {
            URL url = new URL(apiUrl);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestProperty("authorization", "Bearer " + this.kisToken);
            con.setRequestProperty("appkey", prodAppkey);
            con.setRequestProperty("appsecret", prodAppSecret);
            con.setRequestProperty("tr_id", "FHKST03010100");

            int responseCode = con.getResponseCode();

            if (responseCode == 200) {
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }


        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


//        JSONObject response = new JSONObject(br.readLine());
//        log.debug("[KisUtil][loadStockMonthlyPrice] {}: 결과값", response.toString());

//        String result = response.getString("output");


        String name;
        JSONArray jsonArray;
        ArrayList<PriceCalculationDto> list = new ArrayList<>();

        try {
            JSONObject result = new JSONObject(br.readLine());

            name = result.getJSONObject("output1").getString("hts_kor_isnm");

            jsonArray = new JSONArray(result.getString("output2"));

            log.debug("[KisUtil][loadStockMonthlyPrice] ETF명: {}", name);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);

                String s_date = object.getString("stck_bsop_date");
//            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
//            LocalDate localDate = String.parse(s_date, dateTimeFormatter);

                int price = Integer.parseInt(object.getString("stck_clpr"));

                list.add(PriceCalculationDto.builder()
                        .localDate(s_date)
                        .price(price)
                        .build());
            }

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    /// 비즈니스 로직
    public String today() {
        LocalDate localDate = LocalDate.now();
        String today = localDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return today;
    }
}
