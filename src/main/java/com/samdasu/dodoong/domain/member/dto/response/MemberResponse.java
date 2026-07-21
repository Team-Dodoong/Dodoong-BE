package com.samdasu.dodoong.domain.member.dto.response;

import com.samdasu.dodoong.domain.member.entity.Member;

public record MemberResponse(
        Long id,
        String loginId,
        String nickname,
        String profileImageUrl,
        String introduction,
        int level,
        int experience,
        int coin
) {

    public static MemberResponse from(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getLoginId(),
                member.getNickname(),
                member.getProfileImageUrl(),
                member.getIntroduction(),
                member.getLevel(),
                member.getExperience(),
                member.getCoin()
        );
    }
}