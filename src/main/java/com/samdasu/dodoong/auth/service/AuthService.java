package com.samdasu.dodoong.auth.service;

import com.samdasu.dodoong.auth.dto.SignupRequest;
import com.samdasu.dodoong.global.exception.CustomException;
import com.samdasu.dodoong.global.response.code.ErrorCode;
import com.samdasu.dodoong.member.domain.Member;
import com.samdasu.dodoong.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Member signup(SignupRequest request) {
        validateDuplicateLoginId(request.loginId());

        //비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.password());

        Member member = Member.builder()
                .loginId(request.loginId())
                .encodedPassword(encodedPassword)
                .build();

        return memberRepository.save(member);
    }

    private void validateDuplicateLoginId(String loginId) {
        if (memberRepository.existsByLoginId(loginId)) {
            throw new CustomException(
                    ErrorCode.DUPLICATE_LOGIN_ID
            );
        }
    }
}