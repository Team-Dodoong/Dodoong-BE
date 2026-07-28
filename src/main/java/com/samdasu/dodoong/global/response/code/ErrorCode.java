package com.samdasu.dodoong.global.response.code;

import com.samdasu.dodoong.global.response.base.BaseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode implements BaseCode {
    // 400 BAD REQUEST
    INVALID_FIELD_ERROR(HttpStatus.BAD_REQUEST, "요청 필드 값이 유효하지 않습니다."),
    MISSING_PARAMETER(HttpStatus.BAD_REQUEST, "필수 요청 파라미터가 누락되었습니다."),
    MISSING_HEADER(HttpStatus.BAD_REQUEST, "필수 요청 헤더가 누락되었습니다."),
    TYPE_MISMATCH(HttpStatus.BAD_REQUEST, "요청 값 타입이 올바르지 않습니다."),
    INVALID_REQUEST_BODY(HttpStatus.BAD_REQUEST, "요청 본문이 올바르지 않습니다."),

    // 401 UNAUTHORIZED
    UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, "인증되지 않은 사용자입니다."),

    // 403 FORBIDDEN
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),

    // 409 CONFLICT
    LOCK_TIMEOUT(HttpStatus.CONFLICT, "다른 요청이 처리 중입니다. 잠시 후 다시 시도해주세요."),

    // 404 NOT FOUND
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 리소스를 찾을 수 없습니다."),

    // 500 INTERNAL SERVER ERROR
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부에서 문제가 발생하였습니다."),

    //Auth
    // 401 UNAUTHORIZED
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED,"아이디 또는 비밀번호가 올바르지 않습니다."),
    // 401 UNAUTHORIZED
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다."),
    // 409 CONFLICT
    DUPLICATE_LOGIN_ID(HttpStatus.CONFLICT, "이미 사용 중인 아이디입니다."),

    // Member
    // 400 BAD REQUEST (S3 연결 전 임시)
    PROFILE_IMAGE_UPLOAD_NOT_SUPPORTED(HttpStatus.BAD_REQUEST, "실제 사진 업로드는 아직 지원되지 않습니다."),
    INSUFFICIENT_EXPERIENCE(HttpStatus.BAD_REQUEST, "레벨업에 필요한 경험치가 부족합니다."),
    // 404 NOT FOUND
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하는 사용자가 없습니다."),
    // 409 CONFLICT
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "이미 사용 중인 닉네임입니다."),

    //Character
    // 400 BAD REQUEST
    INSUFFICIENT_COIN(HttpStatus.BAD_REQUEST, "코인이 부족합니다."),

    // 403 FORBIDDEN
    CHARACTER_NOT_OWNED(HttpStatus.FORBIDDEN, "보유하지 않은 캐릭터는 장착할 수 없습니다."),

    // 404 NOT FOUND
    CHARACTER_NOT_FOUND(HttpStatus.NOT_FOUND,"존재하지 않는 캐릭터입니다."),
    EQUIPPED_CHARACTER_NOT_FOUND(HttpStatus.NOT_FOUND, "장착 중인 캐릭터가 존재하지 않습니다."),

    // 409 CONFLICT
    ALREADY_OWNED_CHARACTER(HttpStatus.CONFLICT, "이미 보유 중인 캐릭터입니다."),
    // DailyQuest
    // 400 BAD REQUEST
    ROUTINE_QUEST_DATE_NOT_FOUND(HttpStatus.BAD_REQUEST, "마감일까지 생성할 수 있는 반복 퀘스트 날짜가 없습니다."),

    // Party
    // 400 BAD REQUEST
    PARTY_PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "파티 비밀번호가 일치하지 않습니다."),
    ALREADY_JOINED_PARTY(HttpStatus.BAD_REQUEST, "이미 가입한 파티입니다."),
    PARTY_FULL(HttpStatus.BAD_REQUEST, "파티 정원이 가득 찼습니다."),
    PARTY_RECRUITMENT_CLOSED(HttpStatus.BAD_REQUEST, "모집이 마감된 파티입니다."),
    NOT_JOINED_PARTY(HttpStatus.BAD_REQUEST, "가입하지 않은 파티입니다."),

    // 403 FORBIDDEN
    PARTY_MEMBER_ONLY(HttpStatus.FORBIDDEN, "해당 파티에 가입한 회원만 조회할 수 있습니다."),

    // 404 NOT FOUND
    PARTY_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 파티입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
