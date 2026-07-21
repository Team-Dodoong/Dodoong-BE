package com.samdasu.dodoong.domain.party.controller;

import com.samdasu.dodoong.domain.auth.security.CustomUserPrincipal;
import com.samdasu.dodoong.domain.party.dto.request.PartyRequestDto;
import com.samdasu.dodoong.domain.party.dto.request.PartyUpdateRequestDto;
import com.samdasu.dodoong.domain.party.dto.response.PartyResponseDto;
import com.samdasu.dodoong.domain.party.service.PartyService;
import com.samdasu.dodoong.global.response.dto.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/parties")
public class PartyController {

    private final PartyService partyService;

    @PostMapping
    public BaseResponse<PartyResponseDto> createParty(
            @RequestBody PartyRequestDto requestDto,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        PartyResponseDto response = partyService.createParty(requestDto, principal.memberId());
        return BaseResponse.created(response);
    }

    @PatchMapping("/{partyId}")
    public BaseResponse<PartyResponseDto> updateParty(
            @PathVariable Long partyId,
            @RequestBody PartyUpdateRequestDto requestDto,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        PartyResponseDto response = partyService.updateParty(partyId, requestDto, principal.memberId());
        return BaseResponse.ok(response);
    }

    @DeleteMapping("/{partyId}")
    public BaseResponse<Void> deleteParty(
            @PathVariable Long partyId,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        partyService.deleteParty(partyId, principal.memberId());
        return BaseResponse.noContent();
    }
}
