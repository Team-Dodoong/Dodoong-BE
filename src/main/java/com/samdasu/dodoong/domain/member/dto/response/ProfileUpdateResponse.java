package com.samdasu.dodoong.domain.member.dto.response;

import com.samdasu.dodoong.domain.member.entity.Member;

public record ProfileUpdateResponse(
        String nickname,
        String profileImageUrl,
        String introduction
) {

    public static ProfileUpdateResponse from(Member member) {
        return new ProfileUpdateResponse(
                member.getNickname(),
                member.getProfileImageUrl(),
                member.getIntroduction()
        );
    }
}