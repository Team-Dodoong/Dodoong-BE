package com.samdasu.dodoong.domain.character.dto.response;

import com.samdasu.dodoong.domain.character.entity.MemberCharacter;

public record OwnedCharacterListItemResponse(Long characterId, String name, boolean isEquipped) {
    public static OwnedCharacterListItemResponse from(MemberCharacter memberCharacter){
        return new OwnedCharacterListItemResponse(
                memberCharacter.getCharacterItem().getId(),
                memberCharacter.getCharacterItem().getName(),
                memberCharacter.isEquipped()
        );
    }
}
