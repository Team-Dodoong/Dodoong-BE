package com.samdasu.dodoong.auth.controller;

import com.samdasu.dodoong.auth.dto.*;
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
        AuthResult result = authService.signup(request);

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

    @PostMapping("/login")
    public ResponseEntity<BaseResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request
    ) {
        AuthResult result = authService.login(request);

        ResponseCookie refreshTokenCookie =
                cookieUtil.createRefreshTokenCookie(
                        result.refreshToken()
                );

        LoginResponse response = LoginResponse.of(
                result.memberId(),
                result.loginId(),
                result.accessToken()
        );

        return ResponseEntity
                .status(SuccessCode.OK.getHttpStatus())
                .header(
                        HttpHeaders.SET_COOKIE,
                        refreshTokenCookie.toString()
                )
                .body(BaseResponse.of(
                        SuccessCode.OK,
                        response
                ));
    }

}