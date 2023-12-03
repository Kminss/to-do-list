package com.sparta.todo.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateToDoRequest(
        @NotBlank(message = "제목을 입력해주세요.")
        String title,
        String content
) {
}
