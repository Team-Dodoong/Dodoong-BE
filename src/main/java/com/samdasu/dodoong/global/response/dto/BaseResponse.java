package com.samdasu.dodoong.global.response.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.samdasu.dodoong.global.response.base.BaseCode;
import com.samdasu.dodoong.global.response.code.SuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse<T> {
    private int status;
    private String code;
    private String message;
    private T data;

    public static <T> BaseResponse<T> of(BaseCode baseCode, T data) {
        return new BaseResponse<>(
                baseCode.getHttpStatus().value(),
                baseCode.name(),
                baseCode.getMessage(),
                data
        );
    }

    public static <T> BaseResponse<T> of(BaseCode baseCode) {
        return of(baseCode, null);
    }

    public static <T> BaseResponse<T> ok(T data) {
        return of(SuccessCode.OK, data);
    }

    public static <T> BaseResponse<T> ok() {
        return of(SuccessCode.OK, null);
    }

    public static <T> BaseResponse<T> created(T data) {
        return of(SuccessCode.CREATED, data);
    }

    public static <T> BaseResponse<T> created() {
        return of(SuccessCode.CREATED);
    }

    public static <T> BaseResponse<T> noContent() {
        return of(SuccessCode.NO_CONTENT);
    }
}
