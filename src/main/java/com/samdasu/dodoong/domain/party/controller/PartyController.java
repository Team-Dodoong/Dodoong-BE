package com.samdasu.dodoong.domain.party.controller;

import com.samdasu.dodoong.domain.auth.security.CustomUserPrincipal;
import com.samdasu.dodoong.domain.party.dto.request.PartyRequestDto;
import com.samdasu.dodoong.domain.party.dto.request.PartyUpdateRequestDto;
import com.samdasu.dodoong.domain.party.dto.response.PartyListResponseDto;
import com.samdasu.dodoong.domain.party.dto.response.PartyResponseDto;
import com.samdasu.dodoong.domain.party.entity.PartyCategory;
import com.samdasu.dodoong.domain.party.service.PartyService;
import com.samdasu.dodoong.global.response.dto.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/{partyId}")
    public BaseResponse<PartyResponseDto> getPartyDetail(
            @PathVariable Long partyId
    ) {
        PartyResponseDto responses = partyService.getPartyDetail(partyId);
        return BaseResponse.ok(responses);
    }

    @GetMapping("/my")
    public BaseResponse<Page<PartyListResponseDto>> getMyParties(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<PartyListResponseDto> responses = partyService.getMyParties(principal.memberId(), pageable);
        return BaseResponse.ok(responses);
    }

    @GetMapping("/search")
    public BaseResponse<Page<PartyListResponseDto>> searchParties(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) List<PartyCategory> categories,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<PartyListResponseDto> responses = partyService.searchParties(keyword, categories, pageable);
        return BaseResponse.ok(responses);
    }
}
