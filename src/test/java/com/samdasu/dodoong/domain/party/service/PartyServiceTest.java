package com.samdasu.dodoong.domain.party.service;

import com.samdasu.dodoong.domain.member.entity.Member;
import com.samdasu.dodoong.domain.member.repository.MemberRepository;
import com.samdasu.dodoong.domain.party.dto.request.PartyJoinRequest;
import com.samdasu.dodoong.domain.party.dto.response.PartyJoinResponse;
import com.samdasu.dodoong.domain.party.dto.response.PartyMonthlyMeResponse;
import com.samdasu.dodoong.domain.party.dto.response.PartyVerificationResponse;
import com.samdasu.dodoong.domain.party.entity.Party;
import com.samdasu.dodoong.domain.party.entity.PartyMember;
import com.samdasu.dodoong.domain.party.entity.PartyRole;
import com.samdasu.dodoong.domain.party.entity.PartyVerification;
import com.samdasu.dodoong.domain.party.repository.PartyMemberRepository;
import com.samdasu.dodoong.domain.party.repository.PartyRepository;
import com.samdasu.dodoong.domain.party.repository.PartyVerificationRepository;
import com.samdasu.dodoong.domain.quest.repository.DailyQuestRepository;
import com.samdasu.dodoong.domain.quest.repository.projection.MonthlyPartyParticipationProjection;
import com.samdasu.dodoong.global.exception.CustomException;
import com.samdasu.dodoong.global.response.code.ErrorCode;
import com.samdasu.dodoong.global.storage.FileStorage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PartyServiceTest {

    private static final ZoneId ZONE_KST = ZoneId.of("Asia/Seoul");

    @Mock
    private PartyRepository partyRepository;

    @Mock
    private PartyMemberRepository partyMemberRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private DailyQuestRepository dailyQuestRepository;

    @Mock
    private PartyVerificationRepository partyVerificationRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private FileStorage fileStorage;

    @InjectMocks
    private PartyService partyService;

    @Test
    void joinParty() {
        Long memberId = 7L;
        Long partyId = 3L;
        Party party = createParty(partyId, "매일 알고리즘 풀기", null, 10, true);
        Member member = createMember(memberId, "dodoong");
        PartyMember savedPartyMember = PartyMember.builder()
                .role(PartyRole.MEMBER)
                .member(member)
                .party(party)
                .build();
        ReflectionTestUtils.setField(savedPartyMember, "id", 15L);

        when(partyRepository.findByIdForUpdate(partyId)).thenReturn(Optional.of(party));
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(partyMemberRepository.existsByMemberIdAndPartyId(memberId, partyId)).thenReturn(false);
        when(partyMemberRepository.countByPartyId(partyId)).thenReturn(3L);
        when(partyMemberRepository.save(any(PartyMember.class))).thenReturn(savedPartyMember);

        PartyJoinResponse response = partyService.joinParty(
                memberId,
                partyId,
                new PartyJoinRequest(null)
        );

        assertThat(response.partyMemberId()).isEqualTo(15L);
        assertThat(response.partyId()).isEqualTo(partyId);
        assertThat(response.memberId()).isEqualTo(memberId);
        assertThat(response.partyName()).isEqualTo("매일 알고리즘 풀기");
    }

    @Test
    void joinPartyThrowsWhenPasswordDoesNotMatch() {
        Long memberId = 7L;
        Long partyId = 3L;
        Party party = createParty(
                partyId,
                "비밀 파티",
                "$2a$10$encodedPasswordHash",
                10,
                true
        );
        Member member = createMember(memberId, "dodoong");

        when(partyRepository.findByIdForUpdate(partyId)).thenReturn(Optional.of(party));
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(partyMemberRepository.existsByMemberIdAndPartyId(memberId, partyId)).thenReturn(false);
        when(partyMemberRepository.countByPartyId(partyId)).thenReturn(3L);
        when(passwordEncoder.matches("9999", "$2a$10$encodedPasswordHash")).thenReturn(false);

        assertThatThrownBy(() -> partyService.joinParty(
                memberId,
                partyId,
                new PartyJoinRequest("9999")
        ))
                .isInstanceOf(CustomException.class)
                .extracting(exception -> ((CustomException) exception).getBaseCode())
                .isEqualTo(ErrorCode.PARTY_PASSWORD_MISMATCH);
    }

    @Test
    void joinPartyWithPassword() {
        Long memberId = 7L;
        Long partyId = 3L;
        String encodedPassword = "$2a$10$encodedPasswordHash";
        Party party = createParty(partyId, "비밀 파티", encodedPassword, 10, true);
        Member member = createMember(memberId, "dodoong");
        PartyMember savedPartyMember = PartyMember.builder()
                .role(PartyRole.MEMBER)
                .member(member)
                .party(party)
                .build();
        ReflectionTestUtils.setField(savedPartyMember, "id", 15L);

        when(partyRepository.findByIdForUpdate(partyId)).thenReturn(Optional.of(party));
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(partyMemberRepository.existsByMemberIdAndPartyId(memberId, partyId)).thenReturn(false);
        when(partyMemberRepository.countByPartyId(partyId)).thenReturn(3L);
        when(passwordEncoder.matches("1234", encodedPassword)).thenReturn(true);
        when(partyMemberRepository.save(any(PartyMember.class))).thenReturn(savedPartyMember);

        PartyJoinResponse response = partyService.joinParty(
                memberId,
                partyId,
                new PartyJoinRequest("1234")
        );

        assertThat(response.partyMemberId()).isEqualTo(15L);
        verify(passwordEncoder).matches("1234", encodedPassword);
    }

    @Test
    void joinPartyThrowsWhenAlreadyJoined() {
        Long memberId = 7L;
        Long partyId = 3L;
        Party party = createParty(partyId, "매일 알고리즘 풀기", null, 10, true);
        Member member = createMember(memberId, "dodoong");

        when(partyRepository.findByIdForUpdate(partyId)).thenReturn(Optional.of(party));
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(partyMemberRepository.existsByMemberIdAndPartyId(memberId, partyId)).thenReturn(true);

        assertThatThrownBy(() -> partyService.joinParty(
                memberId,
                partyId,
                new PartyJoinRequest(null)
        ))
                .isInstanceOf(CustomException.class)
                .extracting(exception -> ((CustomException) exception).getBaseCode())
                .isEqualTo(ErrorCode.ALREADY_JOINED_PARTY);
    }

    @Test
    void joinPartyThrowsWhenPartyIsFull() {
        Long memberId = 7L;
        Long partyId = 3L;
        Party party = createParty(partyId, "매일 알고리즘 풀기", null, 3, true);
        Member member = createMember(memberId, "dodoong");

        when(partyRepository.findByIdForUpdate(partyId)).thenReturn(Optional.of(party));
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(partyMemberRepository.existsByMemberIdAndPartyId(memberId, partyId)).thenReturn(false);
        when(partyMemberRepository.countByPartyId(partyId)).thenReturn(3L);

        assertThatThrownBy(() -> partyService.joinParty(
                memberId,
                partyId,
                new PartyJoinRequest(null)
        ))
                .isInstanceOf(CustomException.class)
                .extracting(exception -> ((CustomException) exception).getBaseCode())
                .isEqualTo(ErrorCode.PARTY_FULL);
    }

    @Test
    void leaveParty() {
        Long memberId = 7L;
        Long partyId = 3L;
        Party party = createParty(partyId, "매일 알고리즘 풀기", null, 10, true);
        Member member = createMember(memberId, "dodoong");
        PartyMember partyMember = PartyMember.builder()
                .role(PartyRole.MEMBER)
                .member(member)
                .party(party)
                .build();

        when(partyRepository.findById(partyId)).thenReturn(Optional.of(party));
        when(partyMemberRepository.findByMemberIdAndPartyId(memberId, partyId))
                .thenReturn(Optional.of(partyMember));

        partyService.leaveParty(memberId, partyId);

        verify(partyMemberRepository).delete(partyMember);
    }

    @Test
    void getMyMonthlyPartyStatus() {
        Long memberId = 7L;
        Long partyId = 3L;
        Party party = createParty(partyId, "매일 알고리즘 풀기", null, 10, true);

        when(partyRepository.findById(partyId)).thenReturn(Optional.of(party));
        when(partyMemberRepository.existsByMemberIdAndPartyId(memberId, partyId)).thenReturn(true);
        when(partyMemberRepository.findMemberIdsByPartyId(partyId)).thenReturn(List.of(7L, 8L, 9L));
        when(dailyQuestRepository.countMonthlyPartyParticipations(anyList(), eq("매일 알고리즘 문제 1개 풀기"), any(), any()))
                .thenReturn(List.of(
                        new TestMonthlyPartyParticipationProjection(8L, 10L),
                        new TestMonthlyPartyParticipationProjection(7L, 8L),
                        new TestMonthlyPartyParticipationProjection(9L, 8L)
                ));
        when(dailyQuestRepository.existsCheckedPartyQuest(eq(memberId), eq("매일 알고리즘 문제 1개 풀기"), any()))
                .thenReturn(true);

        PartyMonthlyMeResponse response = partyService.getMyMonthlyPartyStatus(memberId, partyId);
        YearMonth currentMonth = YearMonth.now(ZONE_KST);

        assertThat(response.partyId()).isEqualTo(partyId);
        assertThat(response.partyName()).isEqualTo("매일 알고리즘 풀기");
        assertThat(response.year()).isEqualTo(currentMonth.getYear());
        assertThat(response.month()).isEqualTo(currentMonth.getMonthValue());
        assertThat(response.rank()).isEqualTo(2);
        assertThat(response.totalMemberCount()).isEqualTo(3);
        assertThat(response.monthlyParticipationCount()).isEqualTo(8L);
        assertThat(response.questContent()).isEqualTo("매일 알고리즘 문제 1개 풀기");
        assertThat(response.todayQuestCompleted()).isTrue();
    }

    @Test
    void getMyMonthlyPartyStatusThrowsWhenMemberIsNotInParty() {
        Long memberId = 7L;
        Long partyId = 3L;
        Party party = createParty(partyId, "매일 알고리즘 풀기", null, 10, true);

        when(partyRepository.findById(partyId)).thenReturn(Optional.of(party));
        when(partyMemberRepository.existsByMemberIdAndPartyId(memberId, partyId)).thenReturn(false);

        assertThatThrownBy(() -> partyService.getMyMonthlyPartyStatus(memberId, partyId))
                .isInstanceOf(CustomException.class)
                .extracting(exception -> ((CustomException) exception).getBaseCode())
                .isEqualTo(ErrorCode.PARTY_MEMBER_ONLY);
    }

    @Test
    void createPartyVerification() {
        Long memberId = 7L;
        Long partyId = 3L;
        Party party = createParty(partyId, "미라클 모닝", null, 10, true);
        Member member = createMember(memberId, "dodoong", "은서");
        PartyMember partyMember = createPartyMember(15L, member, party);
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "verification.jpg",
                "image/jpeg",
                "image-content".getBytes()
        );
        PartyVerification verification = PartyVerification.builder()
                .party(party)
                .partyMember(partyMember)
                .imageUrl("https://example.com/verifications/24.jpg")
                .verified(true)
                .verificationDate(LocalDate.now(ZONE_KST))
                .build();
        ReflectionTestUtils.setField(verification, "id", 24L);
        ReflectionTestUtils.setField(verification, "createdAt", LocalDateTime.of(2026, 7, 13, 23, 26, 41));

        when(partyRepository.findById(partyId)).thenReturn(Optional.of(party));
        when(partyMemberRepository.findByMemberIdAndPartyIdForUpdate(memberId, partyId))
                .thenReturn(Optional.of(partyMember));
        when(partyVerificationRepository.existsByPartyMemberIdAndVerificationDate(eq(15L), any(LocalDate.class)))
                .thenReturn(false);
        when(fileStorage.upload(image, "party-verifications/3/7"))
                .thenReturn("https://example.com/verifications/24.jpg");
        when(partyVerificationRepository.save(any(PartyVerification.class))).thenReturn(verification);

        PartyVerificationResponse response = partyService.createPartyVerification(memberId, partyId, image);

        assertThat(response.verificationId()).isEqualTo(24L);
        assertThat(response.partyId()).isEqualTo(3L);
        assertThat(response.partyMemberId()).isEqualTo(15L);
        assertThat(response.memberId()).isEqualTo(7L);
        assertThat(response.nickname()).isEqualTo("은서");
        assertThat(response.imageUrl()).isEqualTo("https://example.com/verifications/24.jpg");
        assertThat(response.verified()).isTrue();
        assertThat(response.createdAt()).isEqualTo(LocalDateTime.of(2026, 7, 13, 23, 26, 41));
    }

    @Test
    void createPartyVerificationThrowsWhenImageMissing() {
        assertThatThrownBy(() -> partyService.createPartyVerification(7L, 3L, null))
                .isInstanceOf(CustomException.class)
                .extracting(exception -> ((CustomException) exception).getBaseCode())
                .isEqualTo(ErrorCode.PARTY_VERIFICATION_IMAGE_REQUIRED);
    }

    @Test
    void createPartyVerificationThrowsWhenAlreadyVerifiedToday() {
        Long memberId = 7L;
        Long partyId = 3L;
        Party party = createParty(partyId, "미라클 모닝", null, 10, true);
        Member member = createMember(memberId, "dodoong", "은서");
        PartyMember partyMember = createPartyMember(15L, member, party);
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "verification.jpg",
                "image/jpeg",
                "image-content".getBytes()
        );

        when(partyRepository.findById(partyId)).thenReturn(Optional.of(party));
        when(partyMemberRepository.findByMemberIdAndPartyIdForUpdate(memberId, partyId))
                .thenReturn(Optional.of(partyMember));
        when(partyVerificationRepository.existsByPartyMemberIdAndVerificationDate(eq(15L), any(LocalDate.class)))
                .thenReturn(true);

        assertThatThrownBy(() -> partyService.createPartyVerification(memberId, partyId, image))
                .isInstanceOf(CustomException.class)
                .extracting(exception -> ((CustomException) exception).getBaseCode())
                .isEqualTo(ErrorCode.PARTY_ALREADY_VERIFIED_TODAY);

        verify(fileStorage, never()).upload(any(), any());
    }

    private Party createParty(
            Long partyId,
            String name,
            String partyPassword,
            int maxMembers,
            boolean isRecruiting
    ) {
        Party party = Party.builder()
                .name(name)
                .maxMembers(maxMembers)
                .isRecruiting(isRecruiting)
                .isPublic(true)
                .partyPassword(partyPassword)
                .questContent("매일 알고리즘 문제 1개 풀기")
                .build();
        ReflectionTestUtils.setField(party, "id", partyId);
        return party;
    }

    private PartyMember createPartyMember(Long partyMemberId, Member member, Party party) {
        PartyMember partyMember = PartyMember.builder()
                .role(PartyRole.MEMBER)
                .member(member)
                .party(party)
                .build();
        ReflectionTestUtils.setField(partyMember, "id", partyMemberId);
        return partyMember;
    }

    private Member createMember(Long memberId, String loginId) {
        return createMember(memberId, loginId, null);
    }

    private Member createMember(Long memberId, String loginId, String nickname) {
        Member member = Member.builder()
                .loginId(loginId)
                .encodedPassword("encodedPassword")
                .build();
        ReflectionTestUtils.setField(member, "id", memberId);
        ReflectionTestUtils.setField(member, "nickname", nickname);
        return member;
    }

    private record TestMonthlyPartyParticipationProjection(
            Long memberId,
            Long participationCount
    ) implements MonthlyPartyParticipationProjection {

        @Override
        public Long getMemberId() {
            return memberId;
        }

        @Override
        public Long getParticipationCount() {
            return participationCount;
        }
    }
}
