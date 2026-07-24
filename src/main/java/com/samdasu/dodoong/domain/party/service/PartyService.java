package com.samdasu.dodoong.domain.party.service;

import com.samdasu.dodoong.domain.member.entity.Member;
import com.samdasu.dodoong.domain.member.repository.MemberRepository;
import com.samdasu.dodoong.domain.party.dto.request.PartyJoinRequest;
import com.samdasu.dodoong.domain.party.dto.response.PartyJoinResponse;
import com.samdasu.dodoong.domain.party.dto.response.PartyMonthlyMeResponse;
import com.samdasu.dodoong.domain.party.entity.Party;
import com.samdasu.dodoong.domain.party.entity.PartyMember;
import com.samdasu.dodoong.domain.party.entity.PartyRole;
import com.samdasu.dodoong.domain.party.repository.PartyMemberRepository;
import com.samdasu.dodoong.domain.party.repository.PartyRepository;
import com.samdasu.dodoong.domain.quest.repository.DailyQuestRepository;
import com.samdasu.dodoong.domain.quest.repository.projection.MonthlyPartyParticipationProjection;
import com.samdasu.dodoong.global.exception.CustomException;
import com.samdasu.dodoong.global.response.code.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PartyService {

    private static final ZoneId ZONE_KST = ZoneId.of("Asia/Seoul");

    private final PartyRepository partyRepository;
    private final PartyMemberRepository partyMemberRepository;
    private final MemberRepository memberRepository;
    private final DailyQuestRepository dailyQuestRepository;

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

        return PartyJoinResponse.from(savedPartyMember);
    }

    @Transactional
    public void leaveParty(Long memberId, Long partyId) {
        findParty(partyId);

        PartyMember partyMember = partyMemberRepository.findByMemberIdAndPartyId(memberId, partyId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_JOINED_PARTY));

        partyMemberRepository.delete(partyMember);
    }

    public PartyMonthlyMeResponse getMyMonthlyPartyStatus(
            Long memberId,
            Long partyId
    ) {
        Party party = findParty(partyId);
        validatePartyMembership(memberId, partyId);

        LocalDate today = LocalDate.now(ZONE_KST);
        YearMonth currentMonth = YearMonth.from(today);
        LocalDate startDate = currentMonth.atDay(1);
        LocalDate endDate = currentMonth.atEndOfMonth();

        List<Long> partyMemberIds = partyMemberRepository.findMemberIdsByPartyId(partyId);

        Map<Long, Long> participationCountMap =
                dailyQuestRepository.countMonthlyPartyParticipations(
                                partyMemberIds,
                                party.getQuestContent(),
                                startDate,
                                endDate
                        )
                        .stream()
                        .collect(Collectors.toMap(
                                MonthlyPartyParticipationProjection::getMemberId,
                                MonthlyPartyParticipationProjection::getParticipationCount
                        ));

        long monthlyParticipationCount = participationCountMap.getOrDefault(memberId, 0L);
        int rank = calculateRank(memberId, partyMemberIds, participationCountMap);
        boolean todayQuestCompleted = dailyQuestRepository.existsCheckedPartyQuest(
                memberId,
                party.getQuestContent(),
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

        if (hasPassword(party) && !Objects.equals(party.getPartyPassword(), extractPassword(request))) {
            throw new CustomException(ErrorCode.PARTY_PASSWORD_MISMATCH);
        }
    }

    private void validatePartyMembership(Long memberId, Long partyId) {
        if (!partyMemberRepository.existsByMemberIdAndPartyId(memberId, partyId)) {
            throw new CustomException(ErrorCode.PARTY_MEMBER_ONLY);
        }
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

    private Party findParty(Long partyId) {
        return partyRepository.findById(partyId)
                .orElseThrow(() -> new CustomException(ErrorCode.PARTY_NOT_FOUND));
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
}
