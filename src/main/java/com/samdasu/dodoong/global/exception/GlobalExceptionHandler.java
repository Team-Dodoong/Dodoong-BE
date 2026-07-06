package com.samdasu.dodoong.global.exception;

import com.samdasu.dodoong.global.response.base.BaseCode;
import com.samdasu.dodoong.global.response.code.ErrorCode;
import com.samdasu.dodoong.global.response.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.nio.file.AccessDeniedException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(
            CustomException e, HttpServletRequest request) {
        BaseCode errorCode = e.getBaseCode();
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ErrorResponse.of(errorCode, request.getRequestURI()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException e, HttpServletRequest request) {
        String detail = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("잘못된 요청입니다.");
        return buildErrorResponse(ErrorCode.INVALID_FIELD_ERROR, request, detail);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            ConstraintViolationException e, HttpServletRequest request) {
        return buildErrorResponse(ErrorCode.INVALID_FIELD_ERROR, request, e.getMessage());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameter(
            MissingServletRequestParameterException e, HttpServletRequest request) {
        return buildErrorResponse(ErrorCode.MISSING_PARAMETER, request, e.getParameterName());
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ErrorResponse> handleMissingHeader(
            MissingRequestHeaderException e, HttpServletRequest request) {
        return buildErrorResponse(ErrorCode.MISSING_HEADER, request, e.getHeaderName());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        String detail = e.getRequiredType() != null
                ? String.format("'%s'은(는) %s 타입이어야 합니다.",
                e.getName(), e.getRequiredType().getSimpleName())
                : "타입 변환 오류입니다.";
        return buildErrorResponse(ErrorCode.TYPE_MISMATCH, request, detail);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(
            HttpMessageNotReadableException e, HttpServletRequest request) {
        return buildErrorResponse(ErrorCode.INVALID_REQUEST_BODY, request, null);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException e, HttpServletRequest request) {
        return buildErrorResponse(ErrorCode.ACCESS_DENIED, request, null);
    }

    // 예상하지 못한 모든 예외
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
            Exception e, HttpServletRequest request) {
        log.error("❌ 예기치 않은 서버 에러 발생 - URI: {}", request.getRequestURI(), e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR, request.getRequestURI()));
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(
            BaseCode errorCode, HttpServletRequest request, Object detail) {
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ErrorResponse.of(errorCode, request.getRequestURI(), detail));
    }
}
