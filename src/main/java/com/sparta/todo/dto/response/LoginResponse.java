package com.sparta.todo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginResponse(
        @Schema(description = "응답 메시지", nullable = false)
        String message
) {
    public static LoginResponse of() {
        return new LoginResponse("로그인에 성공하였습니다.");
    }
}
