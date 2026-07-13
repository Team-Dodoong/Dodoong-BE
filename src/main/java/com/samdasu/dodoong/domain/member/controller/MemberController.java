package com.samdasu.dodoong.domain.member.controller;

import com.samdasu.dodoong.domain.auth.security.CustomUserPrincipal;
import com.samdasu.dodoong.domain.member.dto.response.MemberResponse;
import com.samdasu.dodoong.domain.member.service.MemberService;
import com.samdasu.dodoong.global.response.dto.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/me")
    public ResponseEntity<BaseResponse<MemberResponse>> getMyInfo(
            @AuthenticationPrincipal
            CustomUserPrincipal principal
    ) {
        MemberResponse response =
                memberService.getMyInfo(
                        principal.memberId()
                );

        return ResponseEntity.ok(
                BaseResponse.ok(response)
        );
    }
}