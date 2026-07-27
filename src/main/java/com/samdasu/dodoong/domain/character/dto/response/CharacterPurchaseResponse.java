package com.samdasu.dodoong.domain.character.dto.response;

import com.samdasu.dodoong.domain.character.entity.CharacterItem;
import com.samdasu.dodoong.domain.member.entity.Member;

public record CharacterPurchaseResponse(
        Long characterId,
        String name,
        int price,
        int remainingCoin
) {
    public static CharacterPurchaseResponse of(CharacterItem characterItem, Member member) {
        return new CharacterPurchaseResponse(
                characterItem.getId(),
                characterItem.getName(),
                characterItem.getPrice(),
                member.getCoin()
        );
    }
}