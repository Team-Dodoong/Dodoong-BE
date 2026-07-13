package com.samdasu.dodoong.domain.member.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ProfileUpdateRequest(

        @Size(max = 50, message = "닉네임은 50자 이하여야 합니다.")
        @Pattern(regexp = ".*\\S.*", message = "닉네임은 공백으로만 구성할 수 없습니다.")
        String nickname,

        @Size(max = 255, message = "소개는 255자 이하여야 합니다.")
        String introduction
) {
}