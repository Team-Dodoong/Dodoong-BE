package com.samdasu.dodoong.domain.character.dto.response;

import com.samdasu.dodoong.domain.character.entity.CharacterItem;

public record EquippedCharacterResponse (Long characterId, String name){
    public static EquippedCharacterResponse from(CharacterItem characterItem){
        return new EquippedCharacterResponse(characterItem.getId(), characterItem.getName());
    }
}
