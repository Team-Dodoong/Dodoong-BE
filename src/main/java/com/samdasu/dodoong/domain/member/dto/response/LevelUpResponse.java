package com.samdasu.dodoong.domain.member.dto.response;

import com.samdasu.dodoong.domain.member.entity.Member;

public record LevelUpResponse(int level, int coin, int experience) {
    public static LevelUpResponse from(Member member) {
        return new LevelUpResponse(
                member.getLevel(),
                member.getCoin(),
                member.getExperience()
        );
    }
}