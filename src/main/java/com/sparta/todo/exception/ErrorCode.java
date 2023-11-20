package com.sparta.todo.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    /* 400 BAD_REQUEST : 잘못된 요청 */
    INVALID_VALUE(BAD_REQUEST, "값이 유효하지 않습니다."),
    INVALID_REFRESH_TOKEN(BAD_REQUEST, "리프레시 토큰이 유효하지 않습니다"),
    MISMATCH_REFRESH_TOKEN(BAD_REQUEST, "리프레시 토큰의 유저 정보가 일치하지 않습니다"),

    /* 401 UNAUTHORIZED : 인증되지 않은 사용자 */
    INVALID_AUTH_TOKEN(UNAUTHORIZED, "인증 정보가 없는 토큰입니다"),
    UNAUTHORIZED_MEMBER(UNAUTHORIZED, "회원 인증 정보가 존재하지 않습니다"),
    BAD_CREDENTIAL(UNAUTHORIZED, "인증정보가 일치하지 않습니다."),

    /*403 FORBIDDEN  권한, 자원이 없음 */
    ACCESS_DENIED(FORBIDDEN, "권한이 없습니다."),
    /* 404 NOT_FOUND : Resource 를 찾을 수 없음 */
    MEMBER_NOT_FOUND(NOT_FOUND, "해당 유저 정보를 찾을 수 없습니다"),
    REFRESH_TOKEN_NOT_FOUND(NOT_FOUND, "로그아웃 된 사용자입니다"),
    VALUE_NOT_FOUND(NOT_FOUND, "요청한 값을 찾을 수 없습니다."),
    TO_DO_NOT_FOUND(NOT_FOUND, "할 일을 찾을 수 없습니다."),
    COMMENT_NOT_FOUND(NOT_FOUND, "댓글을 찾을 수 없습니다."),
    TO_DO_COMPLETE_NOT_FOUND(NOT_FOUND, "할 일 완료처리가 되어있지 않습니다."),
    TO_DO_HIDDEN_NOT_FOUND(NOT_FOUND, "할 일 비공개처리가 되어있지 않습니다."),

    /* 409 CONFLICT : Resource 의 현재 상태와 충돌. 보통 중복된 데이터 존재 */
    DUPLICATE_RESOURCE(CONFLICT, "데이터가 이미 존재합니다"),
    ALREADY_COMPLETE_TO_DO(CONFLICT, "이미 완료 처리된 할 일입니다."),
    ALREADY_HIDDEN_TO_DO(CONFLICT, "이미 비공개 처리된 할 일입니다."),
    ALREADY_EXIST_MEMBER(CONFLICT, "이미 존재하는 회원입니다."),


    /* 500 INTERNAL_SERVER_ERROR : 서버 에러 */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "내부 서버 에러입니다.");
    private final HttpStatus httpStatus;
    private final String detail;
}
