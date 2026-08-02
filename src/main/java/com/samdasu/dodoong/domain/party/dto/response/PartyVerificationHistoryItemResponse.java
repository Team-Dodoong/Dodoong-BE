package com.samdasu.dodoong.domain.party.dto.response;

import com.samdasu.dodoong.domain.party.entity.PartyMember;
import com.samdasu.dodoong.domain.party.entity.PartyVerification;

import java.time.LocalDateTime;

public record PartyVerificationHistoryItemResponse(
        Long partyMemberId,
        Long memberId,
        String nickname,
        String profileImageUrl,
        boolean verified,
        Long verificationId,
        String imageUrl,
        LocalDateTime verifiedAt
) {
    public static PartyVerificationHistoryItemResponse of(
            PartyMember partyMember,
            PartyVerification verification,
            String profileImageUrl
    ) {
        boolean verified = verification != null;

        return new PartyVerificationHistoryItemResponse(
                partyMember.getId(),
                partyMember.getMember().getId(),
                partyMember.getMember().getNickname(),
                profileImageUrl,
                verified,
                verified ? verification.getId() : null,
                verified ? verification.getImageUrl() : null,
                verified ? verification.getCreatedAt() : null
        );
    }
}
