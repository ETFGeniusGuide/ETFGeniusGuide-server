package com.donghyun.EGG.api.controller.member;

import com.donghyun.EGG.api.controller.member.response.KisTokenResponse;
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
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Map;

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
    public KisTokenResponse kisToken() throws IOException, JSONException {
        KisTokenResponse accessToken = KisTokenResponse.builder().accessToken(kisUtil.generateKisToken()).build();
        // TODO: 2024-08-12 (012) String 리턴하는 습관 당장 고치기, 토큰 처리 시급
//        log.debug("kisToken: {}", kisUtil.getKisToken());

        return accessToken;
    }

    @GetMapping("/kistest")
    public void test() {
        log.debug("kisToken: {}", kisUtil.getKisToken());
    }

    @GetMapping("/naverlogin")
    public Long naverLoginMember(@RequestParam(value = "code") String code, @RequestParam(value = "state") String state) throws IOException, JSONException {
        // naver 로그인 시도 -> 처음이면 로그인, 로그인 이력이 있으면 통과 -> 로그인이 완료되면 콜백주소인 도메인/naverlogin 호출 ( 현재 함수 == 콜백 함수 )

        WebClient webClient = WebClient.create();

        Mono<String> response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https") // 스킴 지정 (http 또는 https)
                        .host("nid.naver.com") // 호스트 지정
                        .path("/oauth2.0/token")
                        .queryParam("grant_type", "authorization_code")
                        .queryParam("client_id", clientId)
                        .queryParam("client_secret", clientSecret)
                        .queryParam("code", code)
                        .queryParam("state", state)
                        .build())
                .retrieve()
                .bodyToMono(String.class);

        // json에서 token 얻어오기
        JSONObject tokens = new JSONObject(response.block());

        String access_token = tokens.getString("access_token");
        String refresh_token = tokens.getString("refresh_token");
        log.debug("access_token: {}", access_token);
        log.debug("refresh_token: {}", refresh_token);

        // getUserInfo()를 통해 회원정보 가져오기
        JSONObject userInfo = new JSONObject(getUserInfo(access_token)).getJSONObject("response");

        log.debug("이름: {}", userInfo.getString("name"));

        // TODO: 2024-07-23 (023) join이 아닌 sns 로그인 함수로 연결
        return memberService.naverLogin(userInfo);
    }

    private String getUserInfo(String access_token) {

        String header = "Bearer " + access_token;

        try {
            String apiURL = "https://openapi.naver.com/v1/nid/me";

            WebClient webClient = WebClient.create();

            String userInfo = webClient.get()
                    .uri(apiURL)
                    .header("Authorization", header)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.debug("getUserInfo: {}", userInfo);
            return userInfo;
        } catch (Exception e) {
            log.debug("Error: {}", e);
            return "Error";
        }
    }
}
