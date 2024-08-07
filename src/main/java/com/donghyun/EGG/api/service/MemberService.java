package com.donghyun.EGG.api.service;

import com.donghyun.EGG.domain.member.Member;
import com.donghyun.EGG.domain.member.repository.MemberRepository;
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

    public Long naverLogin(JSONObject jsonObject) throws JSONException {
        // TODO: 2024-07-23 (023) joinMember가 아닌 sns로그인 함수로 이름 변경

        String email = jsonObject.getString("email");
        if(memberRepository.existsByEmail(email)) {
            return memberRepository.findByEmail(email).getId();
        }

        Member member = Member.builder()
                .email(email)
                .name(jsonObject.getString("name"))
                .password(jsonObject.getString("mobile"))
                .build();
        // TODO: 2024-07-23 (023) sns계정 회원가입 다시 구현

        return memberRepository.save(member).getId();
    }

}
