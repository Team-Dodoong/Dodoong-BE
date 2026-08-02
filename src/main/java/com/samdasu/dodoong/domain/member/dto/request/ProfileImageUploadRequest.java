package com.samdasu.dodoong.domain.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ProfileImageUploadRequest(
        @NotBlank(message = "Content-Type은 필수입니다.")
        @Pattern(
                regexp = "image/(jpeg|png|webp)",
                message = "jpeg, png, webp 이미지만 업로드할 수 있습니다."
        )
        String contentType
) {
}