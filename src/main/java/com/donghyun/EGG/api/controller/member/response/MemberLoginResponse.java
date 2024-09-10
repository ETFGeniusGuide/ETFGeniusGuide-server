package com.donghyun.EGG.api.controller.member.response;

import lombok.Builder;
import lombok.Data;

@Data
public class MemberLoginResponse {
    private String accessToken;
    private String refreshToken;

    @Builder
    public MemberLoginResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public static MemberLoginResponse of(String accessToken, String refreshToken) {
        return MemberLoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
