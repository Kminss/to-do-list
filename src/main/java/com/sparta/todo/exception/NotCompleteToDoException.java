package com.sparta.todo.exception;

public class NotCompleteToDoException extends ApiException {

    public NotCompleteToDoException() {
        super(ErrorCode.TO_DO_COMPLETE_NOT_FOUND);
    }
}
