package com.donghyun.EGG.api.controller.member.response;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class KisTokenResponse {
    String accessToken;

    @Builder
    public KisTokenResponse(String accessToken) {
        this.accessToken = accessToken;
    }
}
