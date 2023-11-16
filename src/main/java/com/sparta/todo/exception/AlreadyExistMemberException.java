package com.sparta.todo.exception;

public class AlreadyExistMemberException extends ApiException {
    public AlreadyExistMemberException() {

        super(ErrorCode.ALREADY_EXIST_MEMBER);
    }
}
