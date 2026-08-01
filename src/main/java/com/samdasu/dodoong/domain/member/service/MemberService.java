package com.samdasu.dodoong.domain.member.service;

import com.samdasu.dodoong.domain.character.repository.MemberCharacterRepository;
import com.samdasu.dodoong.domain.member.dto.request.ProfileImageUploadRequest;
import com.samdasu.dodoong.domain.member.dto.response.LevelUpResponse;
import com.samdasu.dodoong.domain.member.dto.response.MemberResponse;
import com.samdasu.dodoong.domain.member.dto.request.ProfileUpdateRequest;
import com.samdasu.dodoong.domain.member.dto.response.ProfileImageUploadResponse;
import com.samdasu.dodoong.domain.member.dto.response.ProfileUpdateResponse;
import com.samdasu.dodoong.domain.member.entity.Member;
import com.samdasu.dodoong.domain.member.repository.MemberRepository;
import com.samdasu.dodoong.domain.party.entity.PartyMember;
import com.samdasu.dodoong.domain.party.entity.PartyRole;
import com.samdasu.dodoong.domain.party.repository.PartyMemberRepository;
import com.samdasu.dodoong.domain.quest.repository.DailyQuestRepository;
import com.samdasu.dodoong.domain.routine.repository.RoutineRepository;
import com.samdasu.dodoong.domain.streak.repository.StreakRepository;
import com.samdasu.dodoong.global.exception.CustomException;
import com.samdasu.dodoong.global.response.code.ErrorCode;
import com.samdasu.dodoong.global.storage.FileStorage;
import com.samdasu.dodoong.global.storage.PresignedUpload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private static final String PROFILE_IMAGE_DIRECTORY = "profiles";

    private final MemberRepository memberRepository;
    private final MemberCharacterRepository memberCharacterRepository;
    private final FileStorage fileStorage;
    private final PartyMemberRepository partyMemberRepository;
    private final DailyQuestRepository dailyQuestRepository;
    private final RoutineRepository routineRepository;
    private final StreakRepository streakRepository;

    public MemberResponse getMyInfo(Long memberId) {
        Member member = findMember(memberId);

        String profileImageUrl = createProfileImageUrl(member.getProfileImageKey());

        return MemberResponse.of(member, profileImageUrl);
    }

    //닉네임, 소개, 프로필 이미지 key 최종 저장
    @Transactional
    public ProfileUpdateResponse updateProfile(Long memberId, ProfileUpdateRequest request) {
        Member member = findMember(memberId);

        validateDuplicateNickname(memberId, request.nickname());

        validateProfileImageKey(memberId, request.profileImageKey());

        String previousProfileImageKey = member.getProfileImageKey();

        member.updateProfile(
                request.nickname(),
                request.profileImageKey(),
                request.introduction()
        );

        //새로운 이미지로 변경한 경우 기존 S3 객체 삭제
        if (isProfileImageChanged(previousProfileImageKey, request.profileImageKey())) {
            try {
                fileStorage.delete(previousProfileImageKey);
            } catch (RuntimeException e) {
                log.warn("이전 프로필 이미지 삭제 실패: key={}", previousProfileImageKey, e);
            }
        }

        String profileImageUrl = createProfileImageUrl(member.getProfileImageKey());

        return ProfileUpdateResponse.of(member, profileImageUrl);
    }

    //회원 탈퇴
    @Transactional
    public void withdraw(Long memberId) {
        Member member = findMember(memberId);

        // 파티장으로 참여 중인 파티가 있다면 회원 탈퇴 불가
        validateMemberCanWithdraw(memberId);

        // 일반 파티원으로 참여 중인 모든 파티에서 탈퇴
        leaveAllJoinedParties(memberId);

        dailyQuestRepository.deleteAllByMemberId(memberId);
        routineRepository.deleteAllByMemberId(memberId);
        streakRepository.deleteAllByMemberId(memberId);
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

    //프로필 이미지 업로드용 Presigned URL 발급
    public ProfileImageUploadResponse createProfileImageUploadUrl(Long memberId, ProfileImageUploadRequest request) {
        findMember(memberId);
        String directory = PROFILE_IMAGE_DIRECTORY + "/" + memberId;

        PresignedUpload presignedUpload = fileStorage.createUploadUrl(directory, request.contentType());

        return ProfileImageUploadResponse.from(presignedUpload);
    }

    private void validateProfileImageKey(Long memberId, String profileImageKey) {
        // 사진을 변경하지 않은 경우
        if (profileImageKey == null || profileImageKey.isBlank()) {
            return;
        }

        String expectedPrefix = PROFILE_IMAGE_DIRECTORY + "/" + memberId + "/";

        if (!profileImageKey.startsWith(expectedPrefix)) {
            throw new CustomException(ErrorCode.INVALID_PROFILE_IMAGE_KEY);
        }
    }

    private boolean isProfileImageChanged(String previousProfileImageKey, String newProfileImageKey) {
        return previousProfileImageKey != null
                && !previousProfileImageKey.isBlank()
                && newProfileImageKey != null
                && !newProfileImageKey.isBlank()
                && !previousProfileImageKey.equals(
                newProfileImageKey
        );
    }

    private String createProfileImageUrl(
            String profileImageKey
    ) {
        if (profileImageKey == null || profileImageKey.isBlank()) {
            return null;
        }

        return fileStorage.toPublicUrl(
                profileImageKey
        );
    }

    //파티장으로 참여 중인 파티가 있다면 회원 탈퇴 불가
    private void validateMemberCanWithdraw(Long memberId) {
        boolean leadsParty = partyMemberRepository.existsByMemberIdAndRole(memberId, PartyRole.LEADER);

        if (leadsParty) {
            throw new CustomException(ErrorCode.PARTY_LEADER_CANNOT_WITHDRAW);
        }
    }

    //일반 파티원으로 참여 중인 모든 파티에서 탈퇴
    private void leaveAllJoinedParties(Long memberId) {
        List<PartyMember> joinedPartyMembers = partyMemberRepository.findAllByMemberIdAndRole(memberId, PartyRole.MEMBER);

        for (PartyMember partyMember : joinedPartyMembers) {
            partyMember.getParty().decreaseCurrentMembers();
        }

        partyMemberRepository.deleteAll(joinedPartyMembers);
    }
}