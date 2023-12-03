package com.sparta.todo.exception;

public class InvalidTokenException extends ApiException{
    public InvalidTokenException() {
        super(ErrorCode.INVALID_AUTH_TOKEN);
    }
}
