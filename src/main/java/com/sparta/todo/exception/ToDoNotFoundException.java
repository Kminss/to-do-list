package com.sparta.todo.exception;

public class ToDoNotFoundException extends ApiException {
    public ToDoNotFoundException() {
        super(ErrorCode.TO_DO_NOT_FOUND);
    }
}
