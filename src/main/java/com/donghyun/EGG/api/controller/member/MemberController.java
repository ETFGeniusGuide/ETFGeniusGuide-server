package com.donghyun.EGG.api.controller.member;

import com.donghyun.EGG.api.service.member.MemberService;
import com.donghyun.EGG.security.KisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    @Value("${CLIENT_ID}")
    private String clientId;

    @Value("${CLIENT_SECRET}")
    private String clientSecret;

    private final MemberService memberService;
    private final KisUtil kisUtil;

    @GetMapping("/kis")
    public String test() throws IOException, JSONException {
        String accessToken = kisUtil.generateKisToken();
        return accessToken;
    }

    @GetMapping("/naverlogin")
    public Long naverLoginMember(@RequestParam(value = "code") String code, @RequestParam(value = "state") String state) throws IOException, JSONException {
        // naver 로그인 시도 -> 처음이면 로그인, 로그인 이력이 있으면 통과 -> 로그인이 완료되면 콜백주소인 도메인/naverlogin 호출 ( 현재 함수 == 콜백 함수 )

        // 사용자가 인증 후 권한 받아오기 성공

        // TODO: 2024-07-23 (023) 정보에 대한 로그 전체 다시 작성하기
        log.debug("code: {}", code);
        String apiURL = "https://nid.naver.com/oauth2.0/token?grant_type=authorization_code&";
        apiURL += "client_id=" + clientId;
        apiURL += "&client_secret=" + clientSecret;
        apiURL += "&code=" + code;
        apiURL += "&state=" + state;

        BufferedReader br;
        // TODO: 2024-08-01 (001) webclient 비동기 방식 사용
        try {
            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            int responseCode = con.getResponseCode();

            if (responseCode == 200) { // 정상 호출
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {  // 에러 발생
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }
        } catch (ProtocolException e) {
            throw new RuntimeException(e);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        // json에서 token 얻어오기
        JSONObject tokens = new JSONObject(br.readLine());

        String access_token = tokens.getString("access_token");
        String refresh_token = tokens.getString("refresh_token");
        log.debug("access_token: {}", access_token);
        log.debug("refresh_token: {}", refresh_token);
        br.close();

        // getUserInfo()를 통해 회원정보 가져오기
        JSONObject userInfo = new JSONObject(getUserInfo(access_token)).getJSONObject("response");

        log.debug("이름: {}", userInfo.getString("name"));

        // TODO: 2024-07-23 (023) join이 아닌 sns 로그인 함수로 연결
        return memberService.naverLogin(userInfo);
    }

    private String getUserInfo(String access_token) {
        String header = "Bearer " + access_token; // Bearer 다음에 공백 추가
        try {
            String apiURL = "https://openapi.naver.com/v1/nid/me";
            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Authorization", header);

            int responseCode = con.getResponseCode();
            BufferedReader br;
            if (responseCode == 200) { // 정상 호출
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {  // 에러 발생
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }

            String userInfo = br.readLine();
            log.debug("getUserInfo: {}", userInfo);
            br.readLine();
//            String inputLine;
//            StringBuffer res = new StringBuffer();
//            while ((inputLine = br.readLine()) != null) {
//                res.append(inputLine);
//            }
//            br.close();
            return userInfo;
        } catch (Exception e) {
            log.debug("Error: {}", e);
            return "Error";
        }
    }
}
