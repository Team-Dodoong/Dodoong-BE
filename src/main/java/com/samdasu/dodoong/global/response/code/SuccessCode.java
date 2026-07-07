package com.samdasu.dodoong.global.response.code;

import com.samdasu.dodoong.global.response.base.BaseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SuccessCode implements BaseCode {
    // 200 OK
    OK(HttpStatus.OK, "요청이 성공적으로 처리되었습니다."),

    // 201 CREATED
    CREATED(HttpStatus.CREATED, "리소스가 성공적으로 생성되었습니다."),

    // 204 NO CONTENT
    NO_CONTENT(HttpStatus.NO_CONTENT, "요청이 처리되었으며 반환할 콘텐츠가 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
