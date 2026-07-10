package com.samdasu.dodoong.auth.controller;

import com.samdasu.dodoong.auth.dto.SignupRequest;
import com.samdasu.dodoong.auth.dto.SignupResponse;
import com.samdasu.dodoong.auth.service.AuthService;
import com.samdasu.dodoong.global.response.code.SuccessCode;
import com.samdasu.dodoong.global.response.dto.BaseResponse;
import com.samdasu.dodoong.member.domain.Member;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<BaseResponse<SignupResponse>> signUp(@RequestBody @Valid SignupRequest request){
        Member member = authService.signup(request);
        SignupResponse response = SignupResponse.from(member);

        return ResponseEntity
                .status(SuccessCode.CREATED.getHttpStatus())
                .body(BaseResponse.of(
                        SuccessCode.CREATED,
                        response
                ));
    }
}