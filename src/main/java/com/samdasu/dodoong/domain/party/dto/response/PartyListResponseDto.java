package com.samdasu.dodoong.domain.party.dto.response;

import com.samdasu.dodoong.domain.party.entity.Party;
import com.samdasu.dodoong.domain.party.entity.PartyCategory;
import java.util.List;

public record PartyListResponseDto(
        Long id,
        String name,
        String description,
        List<PartyCategory> categories,
        int currentMembers,
        boolean isFull,
        boolean isPublic
) {
    public static PartyListResponseDto from(Party party) {
        int currentCount = party.getCurrentMembers();

        return new PartyListResponseDto(
                party.getId(),
                party.getName(),
                party.getDescription(),
                party.getCategory(),
                currentCount,
                currentCount >= party.getMaxMembers(),
                party.isPublic()
        );
    }
}