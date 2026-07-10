package com.samdasu.dodoong.auth.dto;

import com.samdasu.dodoong.member.domain.Member;

public record SignupResponse(
        Long memberId,
        String loginId
) {
    public static SignupResponse from(Member member) {
        return new SignupResponse(
                member.getMemberId(),
                member.getLoginId()
        );
    }
}