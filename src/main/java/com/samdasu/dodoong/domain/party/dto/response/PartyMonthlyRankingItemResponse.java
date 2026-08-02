package com.samdasu.dodoong.domain.party.dto.response;

import com.samdasu.dodoong.domain.party.entity.PartyMember;

public record PartyMonthlyRankingItemResponse(
        int rank,
        Long memberId,
        String nickname,
        String profileImageUrl,
        long score,
        long verificationCount
) {
    public static PartyMonthlyRankingItemResponse of(
            int rank,
            PartyMember partyMember,
            long verificationCount
    ) {
        return new PartyMonthlyRankingItemResponse(
                rank,
                partyMember.getMember().getId(),
                partyMember.getMember().getNickname(),
                partyMember.getMember().getProfileImageUrl(),
                verificationCount,
                verificationCount
        );
    }
}
