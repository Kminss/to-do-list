package com.sparta.todo.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ErrorResponse {
    private final LocalDateTime timestamp = LocalDateTime.now();
    private final int status;
    private final String name;
    private final String message;
    private final Object data;

    @Builder
    protected ErrorResponse(int status, String name, String message, Object data) {
        this.status = status;
        this.name = name;
        this.message = message;
        this.data = data;
    }
}
