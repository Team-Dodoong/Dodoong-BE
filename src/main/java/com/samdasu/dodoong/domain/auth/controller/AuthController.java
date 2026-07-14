package com.samdasu.dodoong.domain.auth.controller;

import com.samdasu.dodoong.domain.auth.dto.AuthResult;
import com.samdasu.dodoong.domain.auth.dto.LoginRequest;
import com.samdasu.dodoong.domain.auth.dto.LoginResponse;
import com.samdasu.dodoong.domain.auth.dto.SignupRequest;
import com.samdasu.dodoong.domain.auth.dto.SignupResponse;
import com.samdasu.dodoong.domain.auth.service.AuthService;
import com.samdasu.dodoong.global.util.CookieUtil;
import com.samdasu.dodoong.global.response.code.SuccessCode;
import com.samdasu.dodoong.global.response.dto.BaseResponse;
import jakarta.servlet.http.HttpServletResponse;
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
    public BaseResponse<SignupResponse> signup(
            @Valid @RequestBody SignupRequest request,
            HttpServletResponse httpResponse
    ) {
        AuthResult result = authService.signup(request);
        cookieUtil.addAuthCookies(httpResponse, result.accessToken(), result.refreshToken());
        SignupResponse response = SignupResponse.of(result.id(), request.loginId());

        return BaseResponse.created(response);
    }

    @PostMapping("/login")
    public BaseResponse<LoginResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse httpResponse
    ) {
        AuthResult result = authService.login(request);
        cookieUtil.addAuthCookies(httpResponse, result.accessToken(), result.refreshToken());
        LoginResponse response = LoginResponse.of(result.id(), result.loginId());

        return BaseResponse.ok(response);
    }
}