package com.sparta.todo.exception;

public class CommentNotFoundException extends ApiException {
    public CommentNotFoundException() {
        super(ErrorCode.COMMENT_NOT_FOUND);
    }
}
