package com.samdasu.dodoong.domain.member.service;

import com.samdasu.dodoong.domain.character.entity.MemberCharacter;
import com.samdasu.dodoong.domain.character.repository.MemberCharacterRepository;
import com.samdasu.dodoong.domain.member.dto.response.LevelUpResponse;
import com.samdasu.dodoong.domain.member.dto.response.MemberResponse;
import com.samdasu.dodoong.domain.member.dto.request.ProfileUpdateRequest;
import com.samdasu.dodoong.domain.member.dto.response.ProfileUpdateResponse;
import com.samdasu.dodoong.domain.member.entity.Member;
import com.samdasu.dodoong.domain.member.repository.MemberRepository;
import com.samdasu.dodoong.global.exception.CustomException;
import com.samdasu.dodoong.global.response.code.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberCharacterRepository memberCharacterRepository;

    public MemberResponse getMyInfo(Long memberId) {
        Member member = findMember(memberId);

        return MemberResponse.from(member);
    }

    @Transactional
    public ProfileUpdateResponse updateProfile(
            Long memberId,
            ProfileUpdateRequest request,
            MultipartFile profileImage
    ) {
        Member member = findMember(memberId);

        String nickname = null;
        String introduction = null;

        if (request != null) {
            nickname = request.nickname();
            introduction = request.introduction();
        }

        validateDuplicateNickname(memberId, nickname);

        String profileImageUrl = null;

        if (profileImage != null && !profileImage.isEmpty()) {
            // TODO: S3 연결 후
            throw new CustomException(
                    ErrorCode.PROFILE_IMAGE_UPLOAD_NOT_SUPPORTED
            );

            // profileImageUrl = fileStorage.upload(profileImage);
        }

        member.updateProfile(
                nickname,
                profileImageUrl,
                introduction
        );

        return ProfileUpdateResponse.from(member);
    }

    //회원 탈퇴
    // TODO: 다른 회원 연관 도메인 구현 후 탈퇴 시 연관 데이터 전체 삭제 구현
    @Transactional
    public void withdraw(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        memberCharacterRepository.deleteAllByMemberId(memberId);
        memberRepository.delete(member);
    }

    @Transactional
    public LevelUpResponse levelUp(Long memberId) {
        Member member = findMember(memberId);

        if (!member.canLevelUp()) {
            throw new CustomException(ErrorCode.INSUFFICIENT_EXPERIENCE);
        }

        member.levelUp();

        return LevelUpResponse.from(member);
    }

    private Member findMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() ->
                        new CustomException(
                                ErrorCode.MEMBER_NOT_FOUND
                        )
                );
    }

    private void validateDuplicateNickname(
            Long memberId,
            String nickname
    ) {
        if (nickname == null) {
            return;
        }

        if (memberRepository.existsByNicknameAndIdNot(
                nickname,
                memberId
        )) {
            throw new CustomException(
                    ErrorCode.DUPLICATE_NICKNAME
            );
        }
    }
}