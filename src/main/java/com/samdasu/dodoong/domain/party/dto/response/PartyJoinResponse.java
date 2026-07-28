package com.samdasu.dodoong.domain.party.dto.response;

import com.samdasu.dodoong.domain.party.entity.PartyMember;

public record PartyJoinResponse(
        Long partyMemberId,
        Long partyId,
        Long memberId,
        String partyName
) {
    public static PartyJoinResponse from(PartyMember partyMember) {
        return new PartyJoinResponse(
                partyMember.getId(),
                partyMember.getParty().getId(),
                partyMember.getMember().getId(),
                partyMember.getParty().getName()
        );
    }
}
