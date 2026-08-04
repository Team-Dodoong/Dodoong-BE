package com.samdasu.dodoong.domain.party.service;

import com.samdasu.dodoong.domain.character.repository.MemberCharacterRepository;
import com.samdasu.dodoong.domain.member.entity.Member;
import com.samdasu.dodoong.domain.member.repository.MemberRepository;
import com.samdasu.dodoong.domain.party.dto.request.PartyJoinRequest;
import com.samdasu.dodoong.domain.party.dto.response.PartyJoinResponse;
import com.samdasu.dodoong.domain.party.dto.response.PartyMonthlyMeResponse;
import com.samdasu.dodoong.domain.party.dto.response.PartyMonthlyRankingItemResponse;
import com.samdasu.dodoong.domain.party.dto.response.PartyMonthlyRankingResponse;
import com.samdasu.dodoong.domain.party.dto.request.PartyRequestDto;
import com.samdasu.dodoong.domain.party.dto.request.PartyUpdateRequestDto;
import com.samdasu.dodoong.domain.party.dto.response.PartyListResponseDto;
import com.samdasu.dodoong.domain.party.dto.response.PartyResponseDto;
import com.samdasu.dodoong.domain.party.dto.response.PartyVerificationHistoryItemResponse;
import com.samdasu.dodoong.domain.party.dto.response.PartyVerificationHistoryResponse;
import com.samdasu.dodoong.domain.party.dto.response.PartyVerificationResponse;
import com.samdasu.dodoong.domain.party.entity.Party;
import com.samdasu.dodoong.domain.party.entity.PartyCategory;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PartyService {

    private static final ZoneId ZONE_KST = ZoneId.of("Asia/Seoul");

    private final PartyRepository partyRepository;
    private final PartyMemberRepository partyMemberRepository;
    private final MemberRepository memberRepository;
    private final MemberCharacterRepository memberCharacterRepository;
    private final PartyVerificationRepository partyVerificationRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileStorage fileStorage;

    @Transactional
    public PartyResponseDto createParty(PartyRequestDto requestDto, MultipartFile image, Long memberId) {
        Member member = findMember(memberId);

        String encodedPassword = encodePassword(requestDto.partyPassword());
        Party savedParty = partyRepository.save(requestDto.toEntity(encodedPassword));

        if (image != null && !image.isEmpty()) {
            String imageUrl = fileStorage.upload(
                    image,
                    "parties/" + savedParty.getId()
            );
            savedParty.updateImageUrl(imageUrl);
        }

        PartyMember partyMember = PartyMember.builder()
                .role(PartyRole.LEADER)
                .member(member)
                .party(savedParty)
                .build();
        partyMemberRepository.save(partyMember);

        return PartyResponseDto.from(savedParty);
    }

    @Transactional
    public PartyResponseDto updateParty(Long partyId, PartyUpdateRequestDto requestDto, Long memberId) {
        Party party = findParty(partyId);
        authorizePartyLeader(partyId, memberId);

        String encodedPassword = encodePassword(requestDto.partyPassword());
        party.updateParty(requestDto, encodedPassword);

        return PartyResponseDto.from(party);
    }

    @Transactional
    public void deleteParty(Long partyId, Long memberId) {
        Party party = findParty(partyId);
        authorizePartyLeader(partyId, memberId);

        partyRepository.delete(party);
    }

    @Transactional(readOnly = true)
    public PartyResponseDto getPartyDetail(Long partyId) {
        Party party = findParty(partyId);
        return PartyResponseDto.from(party);
    }

    @Transactional(readOnly = true)
    public Page<PartyListResponseDto> getMyParties(Long memberId, Pageable pageable) {
        Page<Party> partyPage = partyRepository.findMyParties(memberId, pageable);
        return partyPage.map(PartyListResponseDto::from);
    }

    @Transactional(readOnly = true)
    public Page<PartyListResponseDto> searchParties(String keyword, List<PartyCategory> categories, Pageable pageable) {
        return partyRepository.searchParties(keyword, categories, pageable)
                .map(PartyListResponseDto::from);
    }

    @Transactional
    public PartyJoinResponse joinParty(
            Long memberId,
            Long partyId,
            PartyJoinRequest request
    ) {
        Party party = findPartyForUpdate(partyId);
        Member member = findMember(memberId);

        validateJoinEligibility(memberId, party, request);

        PartyMember savedPartyMember = partyMemberRepository.save(
                PartyMember.builder()
                        .role(PartyRole.MEMBER)
                        .member(member)
                        .party(party)
                        .build()
        );

        party.increaseCurrentMembers();

        return PartyJoinResponse.from(savedPartyMember);
    }

    @Transactional
    public void leaveParty(Long memberId, Long partyId) {
        Party party = findPartyForUpdate(partyId);

        PartyMember partyMember = partyMemberRepository.findByMemberIdAndPartyId(memberId, partyId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_JOINED_PARTY));

        //파티장은 파티 탈퇴 불가(deleteParty만 가능)
        if (partyMember.getRole() == PartyRole.LEADER) {
            throw new CustomException(ErrorCode.PARTY_LEADER_CANNOT_LEAVE);
        }

        partyMemberRepository.delete(partyMember);
        party.decreaseCurrentMembers();
    }

    public PartyMonthlyMeResponse getMyMonthlyPartyStatus(
            Long memberId,
            Long partyId
    ) {
        Party party = findParty(partyId);
        PartyMember partyMember = findPartyMember(memberId, partyId);

        LocalDate today = LocalDate.now(ZONE_KST);
        YearMonth currentMonth = YearMonth.from(today);
        LocalDate startDate = currentMonth.atDay(1);
        LocalDate endDate = currentMonth.atEndOfMonth();

        List<Long> partyMemberIds = partyMemberRepository.findMemberIdsByPartyId(partyId);

        Map<Long, Long> participationCountMap =
                partyVerificationRepository.countMonthlyVerificationCounts(
                                partyId,
                                startDate,
                                endDate
                        )
                        .stream()
                        .collect(Collectors.toMap(
                                MonthlyPartyVerificationCountProjection::getMemberId,
                                MonthlyPartyVerificationCountProjection::getVerificationCount
                        ));

        long monthlyParticipationCount = participationCountMap.getOrDefault(memberId, 0L);
        int rank = calculateRank(memberId, partyMemberIds, participationCountMap);
        boolean todayQuestCompleted = partyVerificationRepository.existsByPartyMemberIdAndVerificationDate(
                partyMember.getId(),
                today
        );

        return PartyMonthlyMeResponse.of(
                party,
                currentMonth,
                rank,
                partyMemberIds.size(),
                monthlyParticipationCount,
                todayQuestCompleted
        );
    }

    public PartyMonthlyRankingResponse getPartyMonthlyRanking(
            Long memberId,
            Long partyId
    ) {
        Party party = findParty(partyId);
        validatePartyMonthlyRankingAccess(memberId, partyId);

        LocalDate today = LocalDate.now(ZONE_KST);
        YearMonth currentMonth = YearMonth.from(today);
        LocalDate startDate = currentMonth.atDay(1);
        LocalDate endDate = currentMonth.atEndOfMonth();

        List<PartyMember> partyMembers = partyMemberRepository.findAllByPartyIdWithMember(partyId);
        Map<Long, Long> equippedCharacterIdMap = findEquippedCharacterIdMap(partyMembers);
        Map<Long, Long> verificationCountMap =
                partyVerificationRepository.countMonthlyVerificationCounts(
                                partyId,
                                startDate,
                                endDate
                        )
                        .stream()
                        .collect(Collectors.toMap(
                                MonthlyPartyVerificationCountProjection::getMemberId,
                                MonthlyPartyVerificationCountProjection::getVerificationCount
                        ));

        List<PartyMonthlyRankingItemResponse> rankings = buildMonthlyRankings(
                partyMembers,
                equippedCharacterIdMap,
                verificationCountMap
        );

        return PartyMonthlyRankingResponse.of(
                party,
                currentMonth,
                rankings
        );
    }

    public PartyVerificationHistoryResponse getPartyVerificationHistory(
            Long memberId,
            Long partyId,
            Long cursor,
            int size
    ) {
        validatePartyVerificationHistoryPageSize(size);
        findParty(partyId);
        validatePartyVerificationHistoryAccess(memberId, partyId);

        LocalDate today = LocalDate.now(ZONE_KST);
        List<PartyMember> fetchedPartyMembers = partyMemberRepository.findVerificationPageByPartyId(
                partyId,
                cursor,
                PageRequest.of(0, size + 1)
        );

        boolean hasNext = fetchedPartyMembers.size() > size;
        List<PartyMember> pagePartyMembers = hasNext
                ? fetchedPartyMembers.subList(0, size)
                : fetchedPartyMembers;

        Map<Long, PartyVerification> verificationMap = findVerificationMap(pagePartyMembers, today);

        List<PartyVerificationHistoryItemResponse> verifications = pagePartyMembers.stream()
                .map(partyMember -> {
                    String profileImageUrl = createProfileImageUrl(partyMember.getMember().getProfileImageKey());

                    return PartyVerificationHistoryItemResponse.of(
                            partyMember,
                            verificationMap.get(partyMember.getId()),
                            profileImageUrl
                    );
                })
                .toList();

        Long nextCursor = verifications.isEmpty()
                ? null
                : verifications.get(verifications.size() - 1).partyMemberId();

        return PartyVerificationHistoryResponse.of(
                partyId,
                today,
                Math.toIntExact(partyMemberRepository.countByPartyId(partyId)),
                Math.toIntExact(partyVerificationRepository.countByPartyIdAndVerificationDate(partyId, today)),
                verifications,
                nextCursor,
                hasNext
        );
    }

    @Transactional
    public PartyVerificationResponse createPartyVerification(
            Long memberId,
            Long partyId,
            MultipartFile image
    ) {
        validateVerificationImage(image);

        Party party = findParty(partyId);
        PartyMember partyMember = findPartyMember(memberId, partyId);
        LocalDate today = LocalDate.now(ZONE_KST);

        if (partyVerificationRepository.existsByPartyMemberIdAndVerificationDate(partyMember.getId(), today)) {
            throw new CustomException(ErrorCode.PARTY_ALREADY_VERIFIED_TODAY);
        }

        String imageUrl = fileStorage.upload(
                image,
                "party-verifications/" + partyId + "/" + memberId
        );

        PartyMember lockedPartyMember = findPartyMemberForVerification(memberId, partyId);

        if (partyVerificationRepository.existsByPartyMemberIdAndVerificationDate(lockedPartyMember.getId(), today)) {
            throw new CustomException(ErrorCode.PARTY_ALREADY_VERIFIED_TODAY);
        }

        PartyVerification savedVerification = partyVerificationRepository.save(
                PartyVerification.builder()
                        .party(party)
                        .partyMember(lockedPartyMember)
                        .imageUrl(imageUrl)
                        .verified(true)
                        .verificationDate(today)
                        .build()
        );

        return PartyVerificationResponse.from(savedVerification);
    }

    private void validateJoinEligibility(
            Long memberId,
            Party party,
            PartyJoinRequest request
    ) {
        if (partyMemberRepository.existsByMemberIdAndPartyId(memberId, party.getId())) {
            throw new CustomException(ErrorCode.ALREADY_JOINED_PARTY);
        }

        if (!party.isRecruiting()) {
            throw new CustomException(ErrorCode.PARTY_RECRUITMENT_CLOSED);
        }

        long currentMemberCount = partyMemberRepository.countByPartyId(party.getId());

        if (currentMemberCount >= party.getMaxMembers()) {
            throw new CustomException(ErrorCode.PARTY_FULL);
        }

        String submittedPassword = extractPassword(request);

        if (
                hasPassword(party) &&
                        (submittedPassword == null ||
                                !passwordEncoder.matches(submittedPassword, party.getPartyPassword()))
        ) {
            throw new CustomException(ErrorCode.PARTY_PASSWORD_MISMATCH);
        }
    }

    private void validatePartyVerificationHistoryAccess(Long memberId, Long partyId) {
        if (!partyMemberRepository.existsByMemberIdAndPartyId(memberId, partyId)) {
            throw new CustomException(ErrorCode.PARTY_VERIFICATION_HISTORY_FORBIDDEN);
        }
    }

    private void validatePartyVerificationHistoryPageSize(int size) {
        if (size < 1) {
            throw new CustomException(ErrorCode.INVALID_FIELD_ERROR);
        }
    }

    private void validatePartyMonthlyRankingAccess(Long memberId, Long partyId) {
        if (!partyMemberRepository.existsByMemberIdAndPartyId(memberId, partyId)) {
            throw new CustomException(ErrorCode.PARTY_MONTHLY_RANKING_FORBIDDEN);
        }
    }

    private PartyMember findPartyMember(Long memberId, Long partyId) {
        return partyMemberRepository.findByMemberIdAndPartyId(memberId, partyId)
                .orElseThrow(() -> new CustomException(ErrorCode.PARTY_MEMBER_ONLY));
    }

    private PartyMember findPartyMemberForVerification(Long memberId, Long partyId) {
        return partyMemberRepository.findByMemberIdAndPartyIdForUpdate(memberId, partyId)
                .orElseThrow(() -> new CustomException(ErrorCode.PARTY_MEMBER_ONLY));
    }

    private void validateVerificationImage(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new CustomException(ErrorCode.PARTY_VERIFICATION_IMAGE_REQUIRED);
        }
    }

    private Map<Long, PartyVerification> findVerificationMap(
            List<PartyMember> partyMembers,
            LocalDate verificationDate
    ) {
        List<Long> partyMemberIds = partyMembers.stream()
                .map(PartyMember::getId)
                .toList();

        if (partyMemberIds.isEmpty()) {
            return Map.of();
        }

        return partyVerificationRepository.findByPartyMemberIdsAndVerificationDate(
                        partyMemberIds,
                        verificationDate
                )
                .stream()
                .collect(Collectors.toMap(
                        verification -> verification.getPartyMember().getId(),
                        verification -> verification
                ));
    }

    private List<PartyMonthlyRankingItemResponse> buildMonthlyRankings(
            List<PartyMember> partyMembers,
            Map<Long, Long> equippedCharacterIdMap,
            Map<Long, Long> verificationCountMap
    ) {
        List<MemberMonthlyVerificationStat> sortedStats = partyMembers.stream()
                .map(partyMember -> new MemberMonthlyVerificationStat(
                        partyMember,
                        verificationCountMap.getOrDefault(partyMember.getMember().getId(), 0L)
                ))
                .sorted(
                        Comparator.comparingLong(MemberMonthlyVerificationStat::verificationCount)
                                .reversed()
                                .thenComparing(stat -> stat.partyMember().getMember().getId())
                )
                .toList();

        int currentRank = 0;
        Long previousScore = null;
        List<PartyMonthlyRankingItemResponse> rankings = new java.util.ArrayList<>();

        for (MemberMonthlyVerificationStat stat : sortedStats) {
            if (!Objects.equals(previousScore, stat.verificationCount())) {
                currentRank++;
                previousScore = stat.verificationCount();
            }

            Long characterId = equippedCharacterIdMap.get(
                    stat.partyMember().getMember().getId()
            );

            rankings.add(
                    PartyMonthlyRankingItemResponse.of(
                            currentRank,
                            stat.partyMember(),
                            stat.verificationCount(),
                            characterId
                    )
            );
        }

        return rankings;
    }

    private Map<Long, Long> findEquippedCharacterIdMap(List<PartyMember> partyMembers) {
        List<Long> memberIds = partyMembers.stream()
                .map(partyMember -> partyMember.getMember().getId())
                .toList();

        if (memberIds.isEmpty()) {
            return Map.of();
        }

        return memberCharacterRepository.findEquippedByMemberIds(memberIds)
                .stream()
                .collect(Collectors.toMap(
                        memberCharacter -> memberCharacter.getMember().getId(),
                        memberCharacter -> memberCharacter.getCharacterItem().getId()
                ));
    }

    private int calculateRank(
            Long memberId,
            List<Long> partyMemberIds,
            Map<Long, Long> participationCountMap
    ) {
        List<ParticipationRank> ranking = partyMemberIds.stream()
                .map(id -> new ParticipationRank(id, participationCountMap.getOrDefault(id, 0L)))
                .sorted(
                        Comparator.comparingLong(ParticipationRank::participationCount)
                                .reversed()
                                .thenComparing(ParticipationRank::memberId)
                )
                .toList();

        int currentRank = 0;
        Long previousCount = null;

        for (ParticipationRank participationRank : ranking) {
            if (!Objects.equals(previousCount, participationRank.participationCount())) {
                currentRank++;
                previousCount = participationRank.participationCount();
            }

            if (participationRank.memberId().equals(memberId)) {
                return currentRank;
            }
        }

        throw new CustomException(ErrorCode.PARTY_MEMBER_ONLY);
    }

    private Party findParty(Long partyId){
        return partyRepository.findById(partyId)
                .orElseThrow(() -> new CustomException(ErrorCode.PARTY_NOT_FOUND));
    }

    private void authorizePartyLeader(Long partyId, Long memberId){
        PartyMember partyMember = partyMemberRepository.findByMemberIdAndPartyId(memberId, partyId)
                .orElseThrow(() -> new CustomException(ErrorCode.PARTY_MEMBER_NOT_FOUND));
        if (partyMember.getRole() != PartyRole.LEADER) {
            throw new CustomException(ErrorCode.FORBIDDEN_UPDATE_PARTY);
        }
    }

    private String encodePassword(String rawPassword) {
        if (!StringUtils.hasText(rawPassword)) {
            return null;
        }
        return passwordEncoder.encode(rawPassword);
    }

    private Party findPartyForUpdate(Long partyId) {
        return partyRepository.findByIdForUpdate(partyId)
                .orElseThrow(() -> new CustomException(ErrorCode.PARTY_NOT_FOUND));
    }

    private Member findMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    }

    private boolean hasPassword(Party party) {
        return party.getPartyPassword() != null && !party.getPartyPassword().isBlank();
    }

    private String extractPassword(PartyJoinRequest request) {
        return request == null ? null : request.partyPassword();
    }

    private record ParticipationRank(
            Long memberId,
            long participationCount
    ) {
    }

    private record MemberMonthlyVerificationStat(
            PartyMember partyMember,
            long verificationCount
    ) {
    }

    private String createProfileImageUrl(String profileImageKey) {
        if (profileImageKey == null || profileImageKey.isBlank()) {
            return null;
        }

        return fileStorage.toPublicUrl(profileImageKey);
    }
}
