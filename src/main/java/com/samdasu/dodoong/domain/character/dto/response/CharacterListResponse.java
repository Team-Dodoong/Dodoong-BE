package com.samdasu.dodoong.domain.character.dto.response;

import java.util.List;

public record CharacterListResponse(List<CharacterListItemResponse> characters) {

    public static CharacterListResponse from(List<CharacterListItemResponse> characters) {
        return new CharacterListResponse(characters);
    }
}