package com.samdasu.dodoong.domain.character.dto.response;

import com.samdasu.dodoong.domain.character.entity.CharacterItem;

public record CharacterDetailResponse(
        Long characterId,
        String name,
        String summary,
        String description,
        int price,
        boolean owned,
        boolean isEquipped
) {
    public static CharacterDetailResponse of(
            CharacterItem characterItem,
            boolean owned,
            boolean isEquipped
    ) {
        return new CharacterDetailResponse(
                characterItem.getId(),
                characterItem.getName(),
                characterItem.getSummary(),
                characterItem.getDescription(),
                characterItem.getPrice(),
                owned,
                isEquipped
        );
    }
}