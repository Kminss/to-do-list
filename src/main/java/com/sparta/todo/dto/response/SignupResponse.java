package com.sparta.todo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record SignupResponse(
        @Schema(
                description = "응답 메시지",
                nullable = false,
                example = "회원가입에 성공하였습니다."
        )
        String message
) {
    public static SignupResponse of () {
        return new SignupResponse("회원가입에 성공하였습니다.");
    }
}
