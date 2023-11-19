package com.sparta.todo.dto.request;

import com.sparta.todo.domain.Member;
import com.sparta.todo.domain.ToDo;
import jakarta.validation.constraints.NotBlank;

public record UpdateToDoRequest(
        @NotBlank(message = "제목을 입력해주세요.")
        String title,
        String content
) {
    public ToDo toEntity(Member member) {
        return ToDo.of(title, content, member);
    }
}
