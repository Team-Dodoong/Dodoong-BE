package com.samdasu.dodoong.domain.party.dto.request;

public record PartyUpdateRequestDto(
        String description,
        Integer maxMembers,
        Boolean isPublic,
        String partyPassword,
        String imageUrl
) {
}