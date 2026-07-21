package com.samdasu.dodoong.domain.character.controller;

import com.samdasu.dodoong.domain.auth.security.CustomUserPrincipal;
import com.samdasu.dodoong.domain.character.dto.response.*;
import com.samdasu.dodoong.domain.character.service.CharacterService;
import com.samdasu.dodoong.global.response.dto.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    //보유 캐릭터 조회
    @GetMapping("/me")
    public BaseResponse<CharacterListResponse> getOwnedCharacters(@AuthenticationPrincipal CustomUserPrincipal principal){
        CharacterListResponse response = characterService.getOwnedCharacters(principal.memberId());

        return BaseResponse.ok(response);
    }

    //캐릭터 구매
    @PostMapping("/{characterId}")
    public BaseResponse<CharacterPurchaseResponse> purchaseCharacter(@AuthenticationPrincipal CustomUserPrincipal principal, @PathVariable Long characterId){
        CharacterPurchaseResponse response = characterService.purchaseCharacter(principal.memberId(), characterId);

        return BaseResponse.created(response);
    }

    //캐릭터 장착
    @PatchMapping("/{characterId}")
    public BaseResponse<CharacterEquipResponse> equipCharacter(@AuthenticationPrincipal CustomUserPrincipal principal, @PathVariable Long characterId){
        CharacterEquipResponse response = characterService.equipCharacter(principal.memberId(), characterId);

        return BaseResponse.ok(response);
    }
}