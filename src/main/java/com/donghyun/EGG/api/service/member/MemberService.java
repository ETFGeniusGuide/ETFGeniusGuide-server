package com.donghyun.EGG.api.service.member;

import com.donghyun.EGG.api.controller.member.response.MemberLoginResponse;
import com.donghyun.EGG.domain.member.Member;
import com.donghyun.EGG.domain.member.repository.MemberRepository;
import com.donghyun.EGG.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    public MemberLoginResponse naverLogin(JSONObject userInfo) throws JSONException {

        String email = userInfo.getString("email");
        log.debug("이메일: {}", userInfo.getString("email"));

        String name = userInfo.getString("name");
        log.debug("이름: {}", userInfo.getString("name"));

        if(memberRepository.existsByEmail(email)) {
            log.info("{} :이전에 네이버 소셜로그인했던 이메일 입니다.", email);
        }
        else {
            Member member = Member.builder()
                    .email(email)
                    .name(userInfo.getString("name"))
                    .password(userInfo.getString("mobile"))
                    .build();
            memberRepository.save(member).getId();
            log.info("[naverLogin] {}: 회원가입 성공했습니다!", email);
        }

        // 사용자의 이메일, 이름을 바탕으로 jwt 생성
        return jwtUtil.generateAllToken(email, name);
    }

}
