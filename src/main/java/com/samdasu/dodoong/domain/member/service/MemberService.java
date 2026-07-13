package com.samdasu.dodoong.domain.member.service;

import com.samdasu.dodoong.domain.member.dto.response.MemberResponse;
import com.samdasu.dodoong.domain.member.dto.request.ProfileUpdateRequest;
import com.samdasu.dodoong.domain.member.dto.response.ProfileUpdateResponse;
import com.samdasu.dodoong.domain.member.entity.Member;
import com.samdasu.dodoong.domain.member.repository.MemberRepository;
import com.samdasu.dodoong.global.exception.CustomException;
import com.samdasu.dodoong.global.response.code.ErrorCode;
import com.samdasu.dodoong.global.storage.FileStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

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