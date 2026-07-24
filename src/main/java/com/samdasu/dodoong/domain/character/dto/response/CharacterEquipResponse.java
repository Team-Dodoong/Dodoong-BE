package com.samdasu.dodoong.domain.character.dto.response;

import com.samdasu.dodoong.domain.character.entity.MemberCharacter;

public record CharacterEquipResponse(Long characterId, String name, boolean isEquipped) {
    public static CharacterEquipResponse from(MemberCharacter memberCharacter){
        return new CharacterEquipResponse(
                memberCharacter.getCharacterItem().getId(),
                memberCharacter.getCharacterItem().getName(),
                memberCharacter.isEquipped()
        );
    }
}
