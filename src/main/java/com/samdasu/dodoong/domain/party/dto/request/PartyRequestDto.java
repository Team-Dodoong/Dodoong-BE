package com.samdasu.dodoong.domain.party.dto.request;

import com.samdasu.dodoong.domain.party.entity.Party;
import com.samdasu.dodoong.domain.party.entity.PartyCategory;

import java.util.List;

public record PartyRequestDto(
        String name,
        String description,
        List<PartyCategory> categories,
        int maxMembers,
        boolean isPublic,
        String partyPassword,
        String questContent,
        String imageUrl
) {
    public Party toEntity(String encodedPassword) {
        return Party.builder()
                .name(this.name)
                .description(this.description)
                .category(this.categories)
                .imageUrl(this.imageUrl)
                .maxMembers(this.maxMembers)
                .isRecruiting(true)
                .isPublic(this.isPublic)
                .partyPassword(this.isPublic ? null : encodedPassword)
                .questContent(this.questContent)
                .build();
    }
}