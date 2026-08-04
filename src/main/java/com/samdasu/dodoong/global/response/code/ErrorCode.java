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
    ROUTINE_QUEST_CANNOT_BE_POSTPONED(HttpStatus.BAD_REQUEST, "루틴 퀘스트는 미룰 수 없습니다."),
    CHECKED_QUEST_CANNOT_BE_POSTPONED(HttpStatus.BAD_REQUEST, "완료된 퀘스트는 미룰 수 없습니다."),
    DAILY_QUEST_CANNOT_BE_DELETED(HttpStatus.BAD_REQUEST, "완료된 퀘스트는 삭제할 수 없습니다."),
    // 404 NOT FOUND
    DAILY_QUEST_NOT_FOUND(HttpStatus.NOT_FOUND,"존재하지 않는 퀘스트입니다."),
    ROUTINE_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 루틴입니다."),

    // Party
    // 400 BAD REQUEST
    INVALID_MAX_MEMBERS(HttpStatus.BAD_REQUEST, "현재 인원보다 최대인원이 적어 수정할 수 없습니다."),
    PARTY_PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "파티 비밀번호가 일치하지 않습니다."),
    ALREADY_JOINED_PARTY(HttpStatus.BAD_REQUEST, "이미 가입한 파티입니다."),
    PARTY_FULL(HttpStatus.BAD_REQUEST, "파티 정원이 가득 찼습니다."),
    PARTY_RECRUITMENT_CLOSED(HttpStatus.BAD_REQUEST, "모집이 마감된 파티입니다."),
    NOT_JOINED_PARTY(HttpStatus.BAD_REQUEST, "가입하지 않은 파티입니다."),
    PARTY_VERIFICATION_IMAGE_REQUIRED(HttpStatus.BAD_REQUEST, "인증 이미지가 필요합니다."),
    // 403 FORBIDDEN
    FORBIDDEN_UPDATE_PARTY(HttpStatus.FORBIDDEN, "파티 수정 권한이 없습니다."),
    PARTY_MEMBER_ONLY(HttpStatus.FORBIDDEN, "해당 파티에 가입한 회원만 조회할 수 있습니다."),
    PARTY_LEADER_CANNOT_LEAVE(HttpStatus.FORBIDDEN, "파티장은 파티를 탈퇴할 수 없습니다. 파티를 삭제해주세요."),
    PARTY_VERIFICATION_HISTORY_FORBIDDEN(HttpStatus.FORBIDDEN, "해당 파티에 가입한 회원만 인증 기록을 조회할 수 있습니다."),
    PARTY_MONTHLY_RANKING_FORBIDDEN(HttpStatus.FORBIDDEN, "해당 파티에 가입한 회원만 랭킹을 조회할 수 있습니다."),
    // 404 NOT FOUND
    PARTY_NOT_FOUND(HttpStatus.NOT_FOUND,"존재하지 않는 파티입니다."),
    PARTY_MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND,"파티에 해당 회원이 존재하지 않습니다."),
    // 409 CONFLICT
    PARTY_LEADER_CANNOT_WITHDRAW(HttpStatus.CONFLICT, "파티장으로 참여 중인 파티가 있습니다. 해당 파티를 삭제한 후 회원 탈퇴를 진행해주세요."),
    PARTY_ALREADY_VERIFIED_TODAY(HttpStatus.CONFLICT, "오늘 이미 인증을 완료했습니다."),

    // Chat
    // 400 BAD REQUEST
    INVALID_CHAT_DESTINATION(HttpStatus.BAD_REQUEST, "허용되지 않은 구독 경로입니다."),
    // 404 NOT FOUND
    CHAT_MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 메시지입니다."),

    // S3
    // 400 BAD REQUEST
    UNSUPPORTED_IMAGE_TYPE(HttpStatus.BAD_REQUEST, "지원하지 않는 이미지 형식입니다."),
    INVALID_PROFILE_IMAGE_KEY(HttpStatus.BAD_REQUEST,"유효하지 않은 프로필 이미지 key입니다."),
    // 500 INTERNAL SERVER ERROR
    FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
