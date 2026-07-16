package com.samdasu.dodoong.domain.character.controller;

import com.samdasu.dodoong.domain.auth.security.CustomUserPrincipal;
import com.samdasu.dodoong.domain.character.dto.response.CharacterListResponse;
import com.samdasu.dodoong.domain.character.service.CharacterService;
import com.samdasu.dodoong.global.response.dto.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/characters")
public class CharacterController {

    private final CharacterService characterService;

    @GetMapping
    public BaseResponse<CharacterListResponse> getCharacters(@AuthenticationPrincipal CustomUserPrincipal principal) {
        CharacterListResponse response = characterService.getCharacters(principal.memberId());

        return BaseResponse.ok(response);
    }
}