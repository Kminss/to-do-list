package com.sparta.todo.exception;

public class NotHiddenToDoException extends ApiException {

    public NotHiddenToDoException() {
        super(ErrorCode.TO_DO_HIDDEN_NOT_FOUND);
    }
}
