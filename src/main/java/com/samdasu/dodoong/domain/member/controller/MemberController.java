package com.samdasu.dodoong.domain.member.controller;

import com.samdasu.dodoong.domain.auth.security.CustomUserPrincipal;
import com.samdasu.dodoong.domain.auth.service.AuthService;
import com.samdasu.dodoong.domain.member.dto.request.ProfileImageUploadRequest;
import com.samdasu.dodoong.domain.member.dto.request.ProfileUpdateRequest;
import com.samdasu.dodoong.domain.member.dto.response.MemberResponse;
import com.samdasu.dodoong.domain.member.dto.response.ProfileImageUploadResponse;
import com.samdasu.dodoong.domain.member.dto.response.ProfileUpdateResponse;
import com.samdasu.dodoong.domain.member.service.MemberService;
import com.samdasu.dodoong.global.response.dto.BaseResponse;
import com.samdasu.dodoong.global.util.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
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
    private final AuthService authService;
    private final CookieUtil cookieUtil;

    @GetMapping("/me")
    public BaseResponse<MemberResponse> getMyInfo(
            @AuthenticationPrincipal
            CustomUserPrincipal principal
    ) {
        MemberResponse response =
                memberService.getMyInfo(
                        principal.memberId()
                );

        return BaseResponse.ok(response);

    }

    //프로필 이미지 업로드용 Presigned URL 발급
    @PostMapping("/me/profile-image/upload-url")
    public BaseResponse<ProfileImageUploadResponse>
    createProfileImageUploadUrl(@AuthenticationPrincipal CustomUserPrincipal principal,
                                @Valid @RequestBody ProfileImageUploadRequest request) {
        ProfileImageUploadResponse response = memberService.createProfileImageUploadUrl(principal.memberId(), request);

        return BaseResponse.ok(response);
    }

    //닉네임, 소개, 프로필 이미지 key 최종 저장
    @PatchMapping("/me")
    public BaseResponse<ProfileUpdateResponse> updateProfile(@AuthenticationPrincipal CustomUserPrincipal principal,
                                                             @Valid @RequestBody ProfileUpdateRequest request) {
        ProfileUpdateResponse response = memberService.updateProfile(principal.memberId(), request);

        return BaseResponse.ok(response);
    }

    //회원 탈퇴
    @DeleteMapping("/me")
    public BaseResponse<Void> withdraw(@AuthenticationPrincipal CustomUserPrincipal principal,
                                       @CookieValue(name= CookieUtil.ACCESS_TOKEN_COOKIE_NAME, required = false)
                                       String accessToken, HttpServletResponse httpResponse){
        Long memberId = principal.memberId();
        memberService.withdraw(memberId);
        authService.invalidateTokens(memberId, accessToken);
        cookieUtil.deleteAuthCookies(httpResponse);

        return BaseResponse.noContent();
    }

}