package com.samdasu.dodoong.domain.party.service;

import com.samdasu.dodoong.domain.character.entity.CharacterItem;
import com.samdasu.dodoong.domain.character.entity.MemberCharacter;
import com.samdasu.dodoong.domain.character.repository.MemberCharacterRepository;
import com.samdasu.dodoong.domain.member.entity.Member;
import com.samdasu.dodoong.domain.member.repository.MemberRepository;
import com.samdasu.dodoong.domain.party.dto.request.PartyJoinRequest;
import com.samdasu.dodoong.domain.party.dto.response.PartyJoinResponse;
import com.samdasu.dodoong.domain.party.dto.response.PartyMonthlyMeResponse;
import com.samdasu.dodoong.domain.party.dto.response.PartyMonthlyRankingResponse;
import com.samdasu.dodoong.domain.party.dto.response.PartyVerificationHistoryResponse;
import com.samdasu.dodoong.domain.party.dto.response.PartyVerificationResponse;
import com.samdasu.dodoong.domain.party.entity.Party;
import com.samdasu.dodoong.domain.party.entity.PartyMember;
import com.samdasu.dodoong.domain.party.entity.PartyRole;
import com.samdasu.dodoong.domain.party.entity.PartyVerification;
import com.samdasu.dodoong.domain.party.repository.PartyMemberRepository;
import com.samdasu.dodoong.domain.party.repository.PartyRepository;
import com.samdasu.dodoong.domain.party.repository.PartyVerificationRepository;
import com.samdasu.dodoong.domain.party.repository.projection.MonthlyPartyVerificationCountProjection;
import com.samdasu.dodoong.global.exception.CustomException;
import com.samdasu.dodoong.global.response.code.ErrorCode;
import com.samdasu.dodoong.global.storage.FileStorage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.InOrder;
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
    private MemberCharacterRepository memberCharacterRepository;

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

        when(partyRepository.findByIdForUpdate(partyId)).thenReturn(Optional.of(party));
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
        PartyMember partyMember = createPartyMember(
                15L,
                createMember(memberId, "eunseo", "은서"),
                party
        );

        when(partyRepository.findById(partyId)).thenReturn(Optional.of(party));
        when(partyMemberRepository.findByMemberIdAndPartyId(memberId, partyId))
                .thenReturn(Optional.of(partyMember));
        when(partyMemberRepository.findMemberIdsByPartyId(partyId)).thenReturn(List.of(7L, 8L, 9L));
        when(partyVerificationRepository.countMonthlyVerificationCounts(eq(partyId), any(), any()))
                .thenReturn(List.of(
                        new TestMonthlyPartyVerificationCountProjection(8L, 10L),
                        new TestMonthlyPartyVerificationCountProjection(7L, 8L),
                        new TestMonthlyPartyVerificationCountProjection(9L, 8L)
                ));
        when(partyVerificationRepository.existsByPartyMemberIdAndVerificationDate(eq(15L), any(LocalDate.class)))
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
        when(partyMemberRepository.findByMemberIdAndPartyId(memberId, partyId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> partyService.getMyMonthlyPartyStatus(memberId, partyId))
                .isInstanceOf(CustomException.class)
                .extracting(exception -> ((CustomException) exception).getBaseCode())
                .isEqualTo(ErrorCode.PARTY_MEMBER_ONLY);
    }

    @Test
    void getPartyMonthlyRanking() {
        Long memberId = 7L;
        Long partyId = 3L;
        Party party = createParty(partyId, "매일 알고리즘 풀기", null, 10, true);
        YearMonth currentMonth = YearMonth.now(ZONE_KST);

        PartyMember partyMember1 = createPartyMember(
                15L,
                createMember(7L, "eunseo", "은서",  "profiles/7/profile.jpg"),
                party
        );
        PartyMember partyMember2 = createPartyMember(
                16L,
                createMember(8L, "minji", "민지", null),
                party
        );
        PartyMember partyMember3 = createPartyMember(
                17L,
                createMember(9L, "yunsu", "윤수",  "profiles/9/profile.jpg"),
                party
        );
        PartyMember partyMember4 = createPartyMember(
                18L,
                createMember(10L, "jiho", "지호", null),
                party
        );
        MemberCharacter equippedCharacter1 = createEquippedMemberCharacter(7L, 101L);
        MemberCharacter equippedCharacter2 = createEquippedMemberCharacter(9L, 303L);

        when(partyRepository.findById(partyId)).thenReturn(Optional.of(party));
        when(partyMemberRepository.existsByMemberIdAndPartyId(memberId, partyId)).thenReturn(true);
        when(partyMemberRepository.findAllByPartyIdWithMember(partyId))
                .thenReturn(List.of(partyMember1, partyMember2, partyMember3, partyMember4));
        when(memberCharacterRepository.findEquippedByMemberIds(List.of(7L, 8L, 9L, 10L)))
                .thenReturn(List.of(equippedCharacter1, equippedCharacter2));
        when(partyVerificationRepository.countMonthlyVerificationCounts(eq(partyId), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of(
                        new TestMonthlyPartyVerificationCountProjection(7L, 12L),
                        new TestMonthlyPartyVerificationCountProjection(8L, 10L),
                        new TestMonthlyPartyVerificationCountProjection(9L, 10L)
                ));

        PartyMonthlyRankingResponse response = partyService.getPartyMonthlyRanking(memberId, partyId);

        assertThat(response.partyId()).isEqualTo(3L);
        assertThat(response.partyName()).isEqualTo("매일 알고리즘 풀기");
        assertThat(response.year()).isEqualTo(currentMonth.getYear());
        assertThat(response.month()).isEqualTo(currentMonth.getMonthValue());
        assertThat(response.rankings()).hasSize(4);
        assertThat(response.rankings().get(0).rank()).isEqualTo(1);
        assertThat(response.rankings().get(0).memberId()).isEqualTo(7L);
        assertThat(response.rankings().get(0).nickname()).isEqualTo("은서");
        assertThat(response.rankings().get(0).characterId()).isEqualTo(101L);
        assertThat(response.rankings().get(0).score()).isEqualTo(12L);
        assertThat(response.rankings().get(0).verificationCount()).isEqualTo(12L);
        assertThat(response.rankings().get(1).rank()).isEqualTo(2);
        assertThat(response.rankings().get(1).memberId()).isEqualTo(8L);
        assertThat(response.rankings().get(1).characterId()).isNull();
        assertThat(response.rankings().get(2).rank()).isEqualTo(2);
        assertThat(response.rankings().get(2).memberId()).isEqualTo(9L);
        assertThat(response.rankings().get(2).characterId()).isEqualTo(303L);
        assertThat(response.rankings().get(3).rank()).isEqualTo(3);
        assertThat(response.rankings().get(3).memberId()).isEqualTo(10L);
        assertThat(response.rankings().get(3).characterId()).isNull();
        assertThat(response.rankings().get(3).score()).isEqualTo(0L);
        assertThat(response.rankings().get(3).verificationCount()).isEqualTo(0L);
    }

    @Test
    void getPartyMonthlyRankingThrowsWhenMemberIsNotInParty() {
        Long memberId = 7L;
        Long partyId = 3L;
        Party party = createParty(partyId, "매일 알고리즘 풀기", null, 10, true);

        when(partyRepository.findById(partyId)).thenReturn(Optional.of(party));
        when(partyMemberRepository.existsByMemberIdAndPartyId(memberId, partyId)).thenReturn(false);

        assertThatThrownBy(() -> partyService.getPartyMonthlyRanking(memberId, partyId))
                .isInstanceOf(CustomException.class)
                .extracting(exception -> ((CustomException) exception).getBaseCode())
                .isEqualTo(ErrorCode.PARTY_MONTHLY_RANKING_FORBIDDEN);
    }

    @Test
    void getPartyVerificationHistory() {
        Long memberId = 7L;
        Long partyId = 3L;
        LocalDate today = LocalDate.now(ZONE_KST);
        Party party = createParty(partyId, "미라클 모닝", null, 10, true);

        Member member1 = createMember(7L, "dodoong", "은서",  "profiles/7/profile.jpg");
        Member member2 = createMember(8L, "minji", "민지", null);
        Member member3 = createMember(9L, "yunsu", "윤수",  "profiles/9/profile.jpg");
        Member member4 = createMember(10L, "jiho", "지호",  "profiles/10/profile.jpg");

        PartyMember partyMember1 = createPartyMember(15L, member1, party);
        PartyMember partyMember2 = createPartyMember(16L, member2, party);
        PartyMember partyMember3 = createPartyMember(17L, member3, party);
        PartyMember partyMember4 = createPartyMember(18L, member4, party);

        PartyVerification verification1 = createVerification(
                24L,
                party,
                partyMember1,
                "https://example.com/verifications/24.jpg",
                LocalDateTime.of(2026, 7, 13, 8, 32, 14),
                today
        );
        PartyVerification verification2 = createVerification(
                25L,
                party,
                partyMember2,
                "https://example.com/verifications/25.jpg",
                LocalDateTime.of(2026, 7, 13, 9, 10, 25),
                today
        );

        when(partyRepository.findById(partyId)).thenReturn(Optional.of(party));
        when(partyMemberRepository.existsByMemberIdAndPartyId(memberId, partyId)).thenReturn(true);
        when(partyMemberRepository.findVerificationPageByPartyId(eq(partyId), isNull(), any()))
                .thenReturn(List.of(partyMember1, partyMember2, partyMember3, partyMember4));
        when(fileStorage.toPublicUrl("profiles/7/profile.jpg"))
                .thenReturn("https://example.com/profiles/7.jpg");
        when(fileStorage.toPublicUrl("profiles/9/profile.jpg"))
                .thenReturn("https://example.com/profiles/9.jpg");
        when(partyVerificationRepository.findByPartyMemberIdsAndVerificationDate(List.of(15L, 16L, 17L), today))
                .thenReturn(List.of(verification1, verification2));
        when(partyMemberRepository.countByPartyId(partyId)).thenReturn(4L);
        when(partyVerificationRepository.countByPartyIdAndVerificationDate(partyId, today)).thenReturn(3L);

        PartyVerificationHistoryResponse response =
                partyService.getPartyVerificationHistory(memberId, partyId, null, 3);

        assertThat(response.partyId()).isEqualTo(3L);
        assertThat(response.date()).isEqualTo(today);
        assertThat(response.totalMemberCount()).isEqualTo(4);
        assertThat(response.verifiedMemberCount()).isEqualTo(3);
        assertThat(response.verifications()).hasSize(3);
        assertThat(response.verifications().get(0).partyMemberId()).isEqualTo(15L);
        assertThat(response.verifications().get(0).nickname()).isEqualTo("은서");
        assertThat(response.verifications().get(0).profileImageUrl()).isEqualTo("https://example.com/profiles/7.jpg");
        assertThat(response.verifications().get(0).verified()).isTrue();
        assertThat(response.verifications().get(0).verificationId()).isEqualTo(24L);
        assertThat(response.verifications().get(0).verifiedAt()).isEqualTo(LocalDateTime.of(2026, 7, 13, 8, 32, 14));
        assertThat(response.verifications().get(2).partyMemberId()).isEqualTo(17L);
        assertThat(response.verifications().get(2).nickname()).isEqualTo("윤수");
        assertThat(response.verifications().get(2).verified()).isFalse();
        assertThat(response.verifications().get(2).verificationId()).isNull();
        assertThat(response.verifications().get(2).imageUrl()).isNull();
        assertThat(response.verifications().get(2).verifiedAt()).isNull();
        assertThat(response.nextCursor()).isEqualTo(17L);
        assertThat(response.hasNext()).isTrue();
    }

    @Test
    void getPartyVerificationHistoryThrowsWhenMemberIsNotInParty() {
        Long memberId = 7L;
        Long partyId = 3L;
        Party party = createParty(partyId, "미라클 모닝", null, 10, true);

        when(partyRepository.findById(partyId)).thenReturn(Optional.of(party));
        when(partyMemberRepository.existsByMemberIdAndPartyId(memberId, partyId)).thenReturn(false);

        assertThatThrownBy(() -> partyService.getPartyVerificationHistory(memberId, partyId, null, 20))
                .isInstanceOf(CustomException.class)
                .extracting(exception -> ((CustomException) exception).getBaseCode())
                .isEqualTo(ErrorCode.PARTY_VERIFICATION_HISTORY_FORBIDDEN);
    }

    @Test
    void getPartyVerificationHistoryThrowsWhenSizeIsLessThanOne() {
        assertThatThrownBy(() -> partyService.getPartyVerificationHistory(7L, 3L, null, 0))
                .isInstanceOf(CustomException.class)
                .extracting(exception -> ((CustomException) exception).getBaseCode())
                .isEqualTo(ErrorCode.INVALID_FIELD_ERROR);

        verifyNoInteractions(partyRepository, partyMemberRepository, partyVerificationRepository);
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
        when(partyMemberRepository.findByMemberIdAndPartyId(memberId, partyId))
                .thenReturn(Optional.of(partyMember));
        when(partyMemberRepository.findByMemberIdAndPartyIdForUpdate(memberId, partyId))
                .thenReturn(Optional.of(partyMember));
        when(memberRepository.findByIdForUpdate(memberId)).thenReturn(Optional.of(member));
        when(partyVerificationRepository.existsByPartyMemberIdAndVerificationDate(eq(15L), any(LocalDate.class)))
                .thenReturn(false, false);
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
        assertThat(response.experience()).isEqualTo(10);
        assertThat(response.canLevelUp()).isFalse();
        assertThat(member.getExperience()).isEqualTo(10);

        InOrder inOrder = inOrder(
                partyRepository,
                partyMemberRepository,
                memberRepository,
                partyVerificationRepository,
                fileStorage
        );
        inOrder.verify(partyRepository).findById(partyId);
        inOrder.verify(partyMemberRepository).findByMemberIdAndPartyId(memberId, partyId);
        inOrder.verify(partyVerificationRepository)
                .existsByPartyMemberIdAndVerificationDate(eq(15L), any(LocalDate.class));
        inOrder.verify(fileStorage).upload(image, "party-verifications/3/7");
        inOrder.verify(partyMemberRepository).findByMemberIdAndPartyIdForUpdate(memberId, partyId);
        inOrder.verify(partyVerificationRepository)
                .existsByPartyMemberIdAndVerificationDate(eq(15L), any(LocalDate.class));
        inOrder.verify(memberRepository).findByIdForUpdate(memberId);
        inOrder.verify(partyVerificationRepository).save(any(PartyVerification.class));
    }

    @Test
    void createPartyVerificationReturnsCanLevelUpWhenExperienceReachesThreshold() {
        Long memberId = 7L;
        Long partyId = 3L;
        Party party = createParty(partyId, "미라클 모닝", null, 10, true);
        Member member = createMember(memberId, "dodoong", "은서");
        ReflectionTestUtils.setField(member, "experience", 190);
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
                .imageUrl("https://example.com/verifications/25.jpg")
                .verified(true)
                .verificationDate(LocalDate.now(ZONE_KST))
                .build();
        ReflectionTestUtils.setField(verification, "id", 25L);
        ReflectionTestUtils.setField(verification, "createdAt", LocalDateTime.of(2026, 7, 14, 6, 10, 0));

        when(partyRepository.findById(partyId)).thenReturn(Optional.of(party));
        when(partyMemberRepository.findByMemberIdAndPartyId(memberId, partyId))
                .thenReturn(Optional.of(partyMember));
        when(partyMemberRepository.findByMemberIdAndPartyIdForUpdate(memberId, partyId))
                .thenReturn(Optional.of(partyMember));
        when(memberRepository.findByIdForUpdate(memberId)).thenReturn(Optional.of(member));
        when(partyVerificationRepository.existsByPartyMemberIdAndVerificationDate(eq(15L), any(LocalDate.class)))
                .thenReturn(false, false);
        when(fileStorage.upload(image, "party-verifications/3/7"))
                .thenReturn("https://example.com/verifications/25.jpg");
        when(partyVerificationRepository.save(any(PartyVerification.class))).thenReturn(verification);

        PartyVerificationResponse response = partyService.createPartyVerification(memberId, partyId, image);

        assertThat(response.experience()).isEqualTo(200);
        assertThat(response.canLevelUp()).isTrue();
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
        when(partyMemberRepository.findByMemberIdAndPartyId(memberId, partyId))
                .thenReturn(Optional.of(partyMember));
        when(partyVerificationRepository.existsByPartyMemberIdAndVerificationDate(eq(15L), any(LocalDate.class)))
                .thenReturn(true);

        assertThatThrownBy(() -> partyService.createPartyVerification(memberId, partyId, image))
                .isInstanceOf(CustomException.class)
                .extracting(exception -> ((CustomException) exception).getBaseCode())
                .isEqualTo(ErrorCode.PARTY_ALREADY_VERIFIED_TODAY);

        verify(fileStorage, never()).upload(any(), any());
        verify(partyMemberRepository, never()).findByMemberIdAndPartyIdForUpdate(any(), any());
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
        return createMember(memberId, loginId, nickname, null);
    }

    private Member createMember(
            Long memberId,
            String loginId,
            String nickname,
            String profileImageKey
    ) {
        Member member = Member.builder()
                .loginId(loginId)
                .encodedPassword("encodedPassword")
                .build();
        ReflectionTestUtils.setField(member, "id", memberId);
        ReflectionTestUtils.setField(member, "nickname", nickname);
        ReflectionTestUtils.setField(member, "profileImageKey", profileImageKey);
        return member;
    }

    private PartyVerification createVerification(
            Long verificationId,
            Party party,
            PartyMember partyMember,
            String imageUrl,
            LocalDateTime createdAt,
            LocalDate verificationDate
    ) {
        PartyVerification verification = PartyVerification.builder()
                .party(party)
                .partyMember(partyMember)
                .imageUrl(imageUrl)
                .verified(true)
                .verificationDate(verificationDate)
                .build();
        ReflectionTestUtils.setField(verification, "id", verificationId);
        ReflectionTestUtils.setField(verification, "createdAt", createdAt);
        return verification;
    }

    private MemberCharacter createEquippedMemberCharacter(Long memberId, Long characterId) {
        Member member = createMember(memberId, "member-" + memberId);
        CharacterItem characterItem = mock(CharacterItem.class);
        when(characterItem.getId()).thenReturn(characterId);

        MemberCharacter memberCharacter = new MemberCharacter(member, characterItem);
        memberCharacter.equip();
        return memberCharacter;
    }

    private record TestMonthlyPartyVerificationCountProjection(
            Long memberId,
            Long verificationCount
    ) implements MonthlyPartyVerificationCountProjection {

        @Override
        public Long getMemberId() {
            return memberId;
        }

        @Override
        public Long getVerificationCount() {
            return verificationCount;
        }
    }
}
