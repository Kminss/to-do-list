package com.sparta.todo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ErrorResponse {
    @Schema(description = "발생일", nullable = false)
    private final LocalDateTime timestamp = LocalDateTime.now();
    @Schema(
            description = "상태코드",
            nullable = false,
            example = "500"
    )
    private final int status;
    @Schema(
            description = "에러명",
            nullable = false,
            example = "INTERNAL_SERVER_ERROR"
    )
    private final String name;
    @Schema(
            description = "에러 메시지",
            nullable = false,
            example = "내부 서버 에러입니다."
    )
    private final String message;
    @Schema(description = "데이터", nullable = true)
    private final Object data;

    @Builder
    protected ErrorResponse(int status, String name, String message, Object data) {
        this.status = status;
        this.name = name;
        this.message = message;
        this.data = data;
    }
}
