package com.samdasu.dodoong.domain.party.dto.request;

import com.samdasu.dodoong.domain.party.entity.Party;
import com.samdasu.dodoong.domain.party.entity.PartyCategory;
import jakarta.validation.constraints.*;

import java.util.List;

public record PartyRequestDto(
        @NotBlank(message = "파티 이름은 필수 입력값입니다.")
        String name,

        @NotBlank(message = "파티 소개는 필수 입력값입니다.")
        String description,

        @NotEmpty(message = "최소 1개 이상의 카테고리를 선택해야 합니다.")
        List<PartyCategory> categories,

        @NotNull
        @Min(value = 2, message = "최대 인원은 최소 2명 이상이어야 합니다.")
        int maxMembers,

        boolean isPublic,

        @Size(min = 4, message = "비밀번호는 4자 이상이여야 합니다.")
        String partyPassword,

        @NotBlank(message = "퀘스트 내용은 필수 입력값입니다.")
        String questContent
) {
    public Party toEntity(String encodedPassword) {
        return Party.builder()
                .name(this.name)
                .description(this.description)
                .category(this.categories)
                .maxMembers(this.maxMembers)
                .isRecruiting(true)
                .isPublic(this.isPublic)
                .partyPassword(this.isPublic ? null : encodedPassword)
                .questContent(this.questContent)
                .build();
    }
}