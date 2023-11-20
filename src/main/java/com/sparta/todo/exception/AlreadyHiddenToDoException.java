package com.sparta.todo.exception;

public class AlreadyHiddenToDoException extends ApiException {
    public AlreadyHiddenToDoException() {

        super(ErrorCode.ALREADY_HIDDEN_TO_DO);
    }
}
