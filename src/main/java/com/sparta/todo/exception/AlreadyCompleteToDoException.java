package com.sparta.todo.exception;

public class AlreadyCompleteToDoException extends ApiException {
    public AlreadyCompleteToDoException() {

        super(ErrorCode.ALREADY_COMPLETE_TO_DO);
    }
}
