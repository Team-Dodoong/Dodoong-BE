package com.samdasu.dodoong.domain.auth.controller;

import com.samdasu.dodoong.domain.auth.dto.AuthResult;
import com.samdasu.dodoong.domain.auth.dto.request.LoginRequest;
import com.samdasu.dodoong.domain.auth.dto.response.LoginResponse;
import com.samdasu.dodoong.domain.auth.dto.request.SignupRequest;
import com.samdasu.dodoong.domain.auth.dto.response.SignupResponse;
import com.samdasu.dodoong.domain.auth.service.AuthService;
import com.samdasu.dodoong.domain.auth.security.CookieUtil;
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

        ResponseCookie accessTokenCookie =
                cookieUtil.createAccessTokenCookie(
                        result.accessToken()
                );

        ResponseCookie refreshTokenCookie =
                cookieUtil.createRefreshTokenCookie(
                        result.refreshToken()
                );

        SignupResponse response = SignupResponse.of(
                result.id(),
                result.loginId()
        );

        return ResponseEntity
                .status(SuccessCode.CREATED.getHttpStatus())
                .header(
                        HttpHeaders.SET_COOKIE,
                        accessTokenCookie.toString(),
                        refreshTokenCookie.toString()
                )
                .body(
                        BaseResponse.of(
                                SuccessCode.CREATED,
                                response
                        )
                );
    }

    @PostMapping("/login")
    public ResponseEntity<BaseResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request
    ) {
        AuthResult result = authService.login(request);

        ResponseCookie accessTokenCookie =
                cookieUtil.createAccessTokenCookie(
                        result.accessToken()
                );

        ResponseCookie refreshTokenCookie =
                cookieUtil.createRefreshTokenCookie(
                        result.refreshToken()
                );

        LoginResponse response = LoginResponse.of(
                result.id(),
                result.loginId()
        );

        return ResponseEntity
                .status(SuccessCode.OK.getHttpStatus())
                .header(
                        HttpHeaders.SET_COOKIE,
                        accessTokenCookie.toString(),
                        refreshTokenCookie.toString()
                )
                .body(
                        BaseResponse.of(
                                SuccessCode.OK,
                                response
                        )
                );
    }


    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(
                    name = CookieUtil.REFRESH_TOKEN_COOKIE_NAME,
                    required = false
            )
            String refreshToken
    ) {
        authService.logout(refreshToken);

        ResponseCookie deletedAccessTokenCookie = cookieUtil.deleteAccessTokenCookie();
        ResponseCookie deletedRefreshTokenCookie = cookieUtil.deleteRefreshTokenCookie();

        return ResponseEntity
                .noContent()
                .header(
                        HttpHeaders.SET_COOKIE,
                        deletedAccessTokenCookie.toString(),
                        deletedRefreshTokenCookie.toString()
                )
                .build();
    }
}