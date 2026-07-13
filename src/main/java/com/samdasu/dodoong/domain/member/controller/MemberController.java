package com.samdasu.dodoong.domain.member.controller;

import com.samdasu.dodoong.domain.auth.security.CustomUserPrincipal;
import com.samdasu.dodoong.domain.member.dto.request.ProfileUpdateRequest;
import com.samdasu.dodoong.domain.member.dto.response.MemberResponse;
import com.samdasu.dodoong.domain.member.dto.response.ProfileUpdateResponse;
import com.samdasu.dodoong.domain.member.service.MemberService;
import com.samdasu.dodoong.global.response.dto.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @PatchMapping(value="/me",consumes = MediaType.MULTIPART_FORM_DATA_VALUE )
    public ResponseEntity<BaseResponse<ProfileUpdateResponse>>
    updateProfile(
            @AuthenticationPrincipal
            CustomUserPrincipal principal,

            @Valid
            @RequestPart(value = "request", required = false)
            ProfileUpdateRequest request,

            @RequestPart(value = "profileImage", required = false)
            MultipartFile profileImage
    ) {
        ProfileUpdateResponse response =
                memberService.updateProfile(
                        principal.memberId(),
                        request,
                        profileImage
                );

        return ResponseEntity.ok(
                BaseResponse.ok(response)
        );
    }
}