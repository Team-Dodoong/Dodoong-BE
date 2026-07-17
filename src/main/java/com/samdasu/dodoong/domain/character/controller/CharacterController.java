package com.samdasu.dodoong.domain.character.controller;

import com.samdasu.dodoong.domain.auth.security.CustomUserPrincipal;
import com.samdasu.dodoong.domain.character.dto.response.CharacterDetailResponse;
import com.samdasu.dodoong.domain.character.dto.response.CharacterListResponse;
import com.samdasu.dodoong.domain.character.dto.response.EquippedCharacterResponse;
import com.samdasu.dodoong.domain.character.service.CharacterService;
import com.samdasu.dodoong.global.response.dto.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/characters")
public class CharacterController {

    private final CharacterService characterService;

    //전체 캐릭터 목록 조회
    @GetMapping
    public BaseResponse<CharacterListResponse> getCharacters(@AuthenticationPrincipal CustomUserPrincipal principal) {
        CharacterListResponse response = characterService.getCharacters(principal.memberId());

        return BaseResponse.ok(response);
    }

    //캐릭터 상세 조회
    @GetMapping("/{characterId}")
    public BaseResponse<CharacterDetailResponse> getCharacter(@AuthenticationPrincipal CustomUserPrincipal principal,
                                                              @PathVariable Long characterId) {
        CharacterDetailResponse response = characterService.getCharacter(principal.memberId(), characterId);

        return BaseResponse.ok(response);
    }

    //장착 중인 캐릭터 조회
    @GetMapping("/me/equipped")
    public BaseResponse<EquippedCharacterResponse> getEquippedCharacter(@AuthenticationPrincipal CustomUserPrincipal principal){
        EquippedCharacterResponse response = characterService.getEquippedCharacter(principal.memberId());

        return BaseResponse.ok(response);
    }
}