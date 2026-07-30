package com.samdasu.dodoong.domain.party.controller;

import com.samdasu.dodoong.domain.auth.security.CustomUserPrincipal;
import com.samdasu.dodoong.domain.party.dto.request.PartyRequestDto;
import com.samdasu.dodoong.domain.party.dto.request.PartyUpdateRequestDto;
import com.samdasu.dodoong.domain.party.dto.response.PartyListResponseDto;
import com.samdasu.dodoong.domain.party.dto.response.PartyMonthlyRankingResponse;
import com.samdasu.dodoong.domain.party.dto.response.PartyResponseDto;
import com.samdasu.dodoong.domain.party.dto.response.PartyVerificationHistoryResponse;
import com.samdasu.dodoong.domain.party.dto.response.PartyVerificationResponse;
import com.samdasu.dodoong.domain.party.entity.PartyCategory;
import com.samdasu.dodoong.domain.party.dto.request.PartyJoinRequest;
import com.samdasu.dodoong.domain.party.dto.response.PartyJoinResponse;
import com.samdasu.dodoong.domain.party.dto.response.PartyMonthlyMeResponse;
import com.samdasu.dodoong.domain.party.service.PartyService;
import com.samdasu.dodoong.global.response.dto.BaseResponse;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/parties")
public class PartyController {

    private final PartyService partyService;

    @PostMapping
    public BaseResponse<PartyResponseDto> createParty(
            @Valid @RequestBody PartyRequestDto requestDto,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        PartyResponseDto response = partyService.createParty(requestDto, principal.memberId());
        return BaseResponse.created(response);
    }

    @PatchMapping("/{partyId}")
    public BaseResponse<PartyResponseDto> updateParty(
            @PathVariable Long partyId,
            @Valid @RequestBody PartyUpdateRequestDto requestDto,
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

    @PostMapping("/{partyId}")
    public BaseResponse<PartyJoinResponse> joinParty(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable Long partyId,
            @RequestBody(required = false) PartyJoinRequest request
    ) {
        PartyJoinResponse response =
                partyService.joinParty(
                        principal.memberId(),
                        partyId,
                        request
                );

        return BaseResponse.ok(response);
    }

    @DeleteMapping("/{partyId}/leave")
    public BaseResponse<Void> leaveParty(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable Long partyId
    ) {
        partyService.leaveParty(principal.memberId(), partyId);

        return BaseResponse.ok();
    }

    @GetMapping("/{partyId}/me/monthly")
    public BaseResponse<PartyMonthlyMeResponse> getMyMonthlyPartyStatus(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable Long partyId
    ) {
        PartyMonthlyMeResponse response =
                partyService.getMyMonthlyPartyStatus(
                        principal.memberId(),
                        partyId
                );

        return BaseResponse.ok(response);
    }

    @GetMapping({"/{partyId}/ranking/monthly", "/{partyId}/rankings/monthly"})
    public BaseResponse<PartyMonthlyRankingResponse> getPartyMonthlyRanking(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable Long partyId
    ) {
        PartyMonthlyRankingResponse response =
                partyService.getPartyMonthlyRanking(
                        principal.memberId(),
                        partyId
                );

        return BaseResponse.ok(response);
    }

    @GetMapping("/{partyId}/verifications")
    public BaseResponse<PartyVerificationHistoryResponse> getPartyVerificationHistory(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable Long partyId,
            @RequestParam(required = false) @Positive Long cursor,
            @RequestParam(defaultValue = "20") @Positive @Max(100) int size
    ) {
        PartyVerificationHistoryResponse response =
                partyService.getPartyVerificationHistory(
                        principal.memberId(),
                        partyId,
                        cursor,
                        size
                );

        return BaseResponse.ok(response);
    }

    @PostMapping(
            value = "/{partyId}/verifications",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public BaseResponse<PartyVerificationResponse> createPartyVerification(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable Long partyId,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        PartyVerificationResponse response =
                partyService.createPartyVerification(
                        principal.memberId(),
                        partyId,
                        image
                );

        return BaseResponse.ok(response);
    }
}
