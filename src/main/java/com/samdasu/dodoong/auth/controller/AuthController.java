package com.samdasu.dodoong.auth.controller;

import com.samdasu.dodoong.auth.dto.SignupRequest;
import com.samdasu.dodoong.auth.dto.SignupResponse;
import com.samdasu.dodoong.auth.dto.SignupResult;
import com.samdasu.dodoong.auth.service.AuthService;
import com.samdasu.dodoong.auth.util.CookieUtil;
import com.samdasu.dodoong.global.response.code.SuccessCode;
import com.samdasu.dodoong.global.response.dto.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final CookieUtil cookieUtil;

    @PostMapping("/signup")
    public ResponseEntity<BaseResponse<SignupResponse>> signup(
            @Valid @RequestBody SignupRequest request
    ) {
        SignupResult result = authService.signup(request);

        ResponseCookie refreshTokenCookie =
                cookieUtil.createRefreshTokenCookie(
                        result.refreshToken()
                );

        SignupResponse response = SignupResponse.of(
                result.memberId(),
                result.loginId(),
                result.accessToken()
        );

        return ResponseEntity
                .status(SuccessCode.CREATED.getHttpStatus())
                .header(
                        HttpHeaders.SET_COOKIE,
                        refreshTokenCookie.toString()
                )
                .body(BaseResponse.of(
                        SuccessCode.CREATED,
                        response
                ));
    }
}