package com.samdasu.dodoong.domain.party.dto.response;

import com.samdasu.dodoong.domain.party.entity.Party;

import java.time.YearMonth;

public record PartyMonthlyMeResponse(
        Long partyId,
        String partyName,
        int year,
        int month,
        int rank,
        int totalMemberCount,
        long monthlyParticipationCount,
        String questContent,
        boolean todayQuestCompleted
) {
    public static PartyMonthlyMeResponse of(
            Party party,
            YearMonth yearMonth,
            int rank,
            int totalMemberCount,
            long monthlyParticipationCount,
            boolean todayQuestCompleted
    ) {
        return new PartyMonthlyMeResponse(
                party.getId(),
                party.getName(),
                yearMonth.getYear(),
                yearMonth.getMonthValue(),
                rank,
                totalMemberCount,
                monthlyParticipationCount,
                party.getQuestContent(),
                todayQuestCompleted
        );
    }
}
