package com.donghyun.EGG.security;

import com.donghyun.EGG.api.service.stock.dto.StockDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

@Slf4j
@Component
public class KisUtil {

    @Value("${PROD_APPKEY}")
    private String prodAppkey;

    @Value("${PROD_APPSECRET}")
    private String prodAppSecret;

    @Value("${PROD}")
    private String prod;


    public String generateKisToken() throws IOException, JSONException {
        String apiURL = prod+"/oauth2/tokenP";

        BufferedReader br;
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

        return accessToken;
    }

    public String loadStockInfo(StockDto stockDto) throws IOException, JSONException {
        String apiUrl = prod + "/uapi/domestic-stock/v1/quotations/search-stock-info?PRDT_TYPE_CD=300";
        apiUrl += "&PDNO=" + stockDto.getPdno();

        BufferedReader br;

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();


            con.setRequestProperty("authorization", "Bearer " + stockDto.getAccess_token());
            con.setRequestProperty("appkey", prodAppkey);
            con.setRequestProperty("appsecret", prodAppSecret);
            con.setRequestProperty("tr_id", "CTPF1002R");
            con.setRequestProperty("custtype", "P");

            int responseCode = con.getResponseCode();

            if(responseCode == 200) {
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            }
            else {
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        JSONObject response = new JSONObject(br.readLine());
        String result = response.getString("output");

        // TODO: 2024-08-10 (010) String이 아닌 주식 정보를 담아서 보내줌. 데이터 정제는 Service에서
        return result;
    }

}
