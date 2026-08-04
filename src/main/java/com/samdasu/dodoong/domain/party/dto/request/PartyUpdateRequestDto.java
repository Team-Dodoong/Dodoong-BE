package com.samdasu.dodoong.domain.party.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PartyUpdateRequestDto(
        String description,

        @Min(value = 2, message = "최대 인원은 최소 2명 이상이어야 합니다.")
        int maxMembers,

        Boolean isPublic,

        @Size(min = 4, message = "비밀번호는 4자 이상이여야 합니다.")
        String partyPassword
) {
}