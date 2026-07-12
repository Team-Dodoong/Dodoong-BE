package com.samdasu.dodoong.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignupRequest(@NotBlank(message="아이디는 필수입니다.")
                            @Size(max = 50, message = "아이디는 50자 이하여야 합니다.")
                            String loginId,
                            @NotBlank(message = "비밀번호는 필수입니다.")
                            @Size(max = 50, message = "비밀번호는 50자 이하여야 합니다.")
                            String password) {

}
