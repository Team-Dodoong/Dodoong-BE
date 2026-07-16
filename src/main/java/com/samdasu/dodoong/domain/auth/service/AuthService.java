package com.samdasu.dodoong.domain.auth.service;

import com.samdasu.dodoong.domain.auth.entity.RefreshToken;
import com.samdasu.dodoong.domain.auth.dto.AuthResult;
import com.samdasu.dodoong.domain.auth.dto.LoginRequest;
import com.samdasu.dodoong.domain.auth.dto.SignupRequest;
import com.samdasu.dodoong.domain.auth.dto.TokenResponse;
import com.samdasu.dodoong.domain.auth.repository.RefreshTokenRepository;
import com.samdasu.dodoong.domain.auth.security.JwtTokenProvider;
import com.samdasu.dodoong.domain.character.entity.CharacterItem;
import com.samdasu.dodoong.domain.character.entity.MemberCharacter;
import com.samdasu.dodoong.domain.character.repository.CharacterItemRepository;
import com.samdasu.dodoong.domain.character.repository.MemberCharacterRepository;
import com.samdasu.dodoong.global.exception.CustomException;
import com.samdasu.dodoong.global.response.code.ErrorCode;
import com.samdasu.dodoong.domain.member.entity.Member;
import com.samdasu.dodoong.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final CharacterItemRepository characterItemRepository;
    private final MemberCharacterRepository memberCharacterRepository;
    private static final Long DEFAULT_CHARACTER_ID = 1L;

    @Transactional
    public AuthResult signup(SignupRequest request) {
        validateDuplicateLoginId(request.loginId());

        String encodedPassword = passwordEncoder.encode(request.password());

        Member member = Member.builder()
                .loginId(request.loginId())
                .encodedPassword(encodedPassword)
                .build();

        Member savedMember = memberRepository.save(member);

        //기본 캐릭터 지급
        CharacterItem defaultCharacter = characterItemRepository.findById(DEFAULT_CHARACTER_ID)
                .orElseThrow(() -> new CustomException(ErrorCode.CHARACTER_NOT_FOUND));

        MemberCharacter memberCharacter = new MemberCharacter(savedMember, defaultCharacter);

        memberCharacter.equip();
        memberCharacterRepository.save(memberCharacter);

        TokenResponse tokenResponse =
                jwtTokenProvider.issueTokens(savedMember);

        saveOrUpdateRefreshToken(
                savedMember,
                tokenResponse.refreshToken()
        );

        return createAuthResult(
                savedMember,
                tokenResponse
        );
    }

    @Transactional
    public AuthResult login(LoginRequest request) {
        Member member = memberRepository
                .findByLoginId(request.loginId())
                .orElseThrow(() ->
                        new CustomException(
                                ErrorCode.INVALID_CREDENTIALS
                        )
                );

        if (!passwordEncoder.matches(
                request.password(),
                member.getPassword()
        )) {
            throw new CustomException(
                    ErrorCode.INVALID_CREDENTIALS
            );
        }

        TokenResponse tokenResponse =
                jwtTokenProvider.issueTokens(member);

        saveOrUpdateRefreshToken(
                member,
                tokenResponse.refreshToken()
        );

        return createAuthResult(
                member,
                tokenResponse
        );
    }

    private void validateDuplicateLoginId(String loginId) {
        if (memberRepository.existsByLoginId(loginId)) {
            throw new CustomException(
                    ErrorCode.DUPLICATE_LOGIN_ID
            );
        }
    }

    private void saveOrUpdateRefreshToken(
            Member member,
            String refreshToken
    ) {
        refreshTokenRepository
                .findByMemberId(member.getId())
                .ifPresentOrElse(
                        savedToken ->
                                savedToken.updateToken(refreshToken),
                        () -> refreshTokenRepository.save(
                                RefreshToken.create(
                                        member,
                                        refreshToken
                                )
                        )
                );
    }

    private AuthResult createAuthResult(
            Member member,
            TokenResponse tokenResponse
    ) {
        return new AuthResult(
                member.getId(),
                member.getLoginId(),
                tokenResponse.accessToken(),
                tokenResponse.refreshToken()
        );
    }
}