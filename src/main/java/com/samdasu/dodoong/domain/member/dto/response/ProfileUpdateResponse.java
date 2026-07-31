package com.samdasu.dodoong.domain.member.dto.response;

import com.samdasu.dodoong.domain.member.entity.Member;

public record ProfileUpdateResponse(
        String nickname,
        String introduction,
        String profileImageUrl
) {

    public static ProfileUpdateResponse of(Member member, String profileImageUrl) {
        return new ProfileUpdateResponse(
                member.getNickname(),
                member.getIntroduction(),
                profileImageUrl
        );
    }
}