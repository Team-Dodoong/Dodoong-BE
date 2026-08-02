package com.samdasu.dodoong.domain.party.dto.response;

import com.samdasu.dodoong.domain.party.entity.PartyVerification;

import java.time.LocalDateTime;

public record PartyVerificationResponse(
        Long verificationId,
        Long partyId,
        Long partyMemberId,
        Long memberId,
        String nickname,
        String imageUrl,
        boolean verified,
        LocalDateTime createdAt
) {
    public static PartyVerificationResponse from(PartyVerification verification) {
        return new PartyVerificationResponse(
                verification.getId(),
                verification.getParty().getId(),
                verification.getPartyMember().getId(),
                verification.getPartyMember().getMember().getId(),
                verification.getPartyMember().getMember().getNickname(),
                verification.getImageUrl(),
                verification.isVerified(),
                verification.getCreatedAt()
        );
    }
}
