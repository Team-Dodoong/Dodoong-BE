package com.samdasu.dodoong.domain.character.dto.response;

import java.util.List;

public record CharacterListResponse<T>(List<T> characters) {

    public static <T> CharacterListResponse<T> from(List<T> characters) {
        return new CharacterListResponse<>(characters);
    }
}