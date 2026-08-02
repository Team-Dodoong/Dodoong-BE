package com.samdasu.dodoong.domain.party.dto.response;

import com.samdasu.dodoong.domain.party.entity.Party;

import java.time.YearMonth;
import java.util.List;

public record PartyMonthlyRankingResponse(
        Long partyId,
        String partyName,
        int year,
        int month,
        List<PartyMonthlyRankingItemResponse> rankings
) {
    public static PartyMonthlyRankingResponse of(
            Party party,
            YearMonth yearMonth,
            List<PartyMonthlyRankingItemResponse> rankings
    ) {
        return new PartyMonthlyRankingResponse(
                party.getId(),
                party.getName(),
                yearMonth.getYear(),
                yearMonth.getMonthValue(),
                rankings
        );
    }
}
