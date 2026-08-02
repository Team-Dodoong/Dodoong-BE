package com.samdasu.dodoong.domain.party.dto.response;

import java.time.LocalDate;
import java.util.List;

public record PartyVerificationHistoryResponse(
        Long partyId,
        LocalDate date,
        int totalMemberCount,
        int verifiedMemberCount,
        List<PartyVerificationHistoryItemResponse> verifications,
        Long nextCursor,
        boolean hasNext
) {
    public static PartyVerificationHistoryResponse of(
            Long partyId,
            LocalDate date,
            int totalMemberCount,
            int verifiedMemberCount,
            List<PartyVerificationHistoryItemResponse> verifications,
            Long nextCursor,
            boolean hasNext
    ) {
        return new PartyVerificationHistoryResponse(
                partyId,
                date,
                totalMemberCount,
                verifiedMemberCount,
                verifications,
                nextCursor,
                hasNext
        );
    }
}
