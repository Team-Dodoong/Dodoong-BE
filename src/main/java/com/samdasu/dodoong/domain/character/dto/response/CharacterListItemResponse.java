package com.samdasu.dodoong.domain.character.dto.response;

import com.samdasu.dodoong.domain.character.entity.CharacterItem;

public record CharacterListItemResponse(
        Long characterId,
        String name,
        int price,
        boolean owned,
        boolean isEquipped
) {
    public static CharacterListItemResponse of(CharacterItem characterItem, boolean owned, boolean isEquipped){
        return new CharacterListItemResponse(
                characterItem.getId(),
                characterItem.getName(),
                characterItem.getPrice(),
                owned,
                isEquipped
        );
    }
}
