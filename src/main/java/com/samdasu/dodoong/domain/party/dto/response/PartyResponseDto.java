package com.samdasu.dodoong.domain.party.dto.response;

import com.samdasu.dodoong.domain.party.entity.Party;
import com.samdasu.dodoong.domain.party.entity.PartyCategory;
import java.time.LocalDateTime;
import java.util.List;

public record PartyResponseDto(
        Long id,
        String name,
        String description,
        List<PartyCategory> categories,
        String imageUrl,
        int currentMembers,
        int maxMembers,
        boolean isRecruiting,
        boolean isPublic,
        String questContent,
        LocalDateTime createdAt
) {
    public static PartyResponseDto from(Party party, String imageUrl) {
        return new PartyResponseDto(
                party.getId(),
                party.getName(),
                party.getDescription(),
                party.getCategory(),
                imageUrl,
                party.getCurrentMembers(),
                party.getMaxMembers(),
                party.isRecruiting(),
                party.isPublic(),
                party.getQuestContent(),
                party.getCreatedAt()
        );
    }
}