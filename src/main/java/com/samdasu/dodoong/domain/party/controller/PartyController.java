package com.samdasu.dodoong.domain.party.controller;

import com.samdasu.dodoong.domain.auth.security.CustomUserPrincipal;
import com.samdasu.dodoong.domain.party.dto.request.PartyJoinRequest;
import com.samdasu.dodoong.domain.party.dto.response.PartyJoinResponse;
import com.samdasu.dodoong.domain.party.dto.response.PartyMonthlyMeResponse;
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

    @DeleteMapping("/{partyId}")
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
}
