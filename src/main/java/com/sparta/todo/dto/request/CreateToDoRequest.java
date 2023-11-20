package com.sparta.todo.dto.request;

import com.sparta.todo.domain.Member;
import com.sparta.todo.domain.ToDo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "ToDo 생성 요청")
public record CreateToDoRequest(
        @NotBlank(message = "제목을 입력해주세요.")
        String title,
        String content
) {
        public ToDo toEntity(Member member) {
                return ToDo.of(title, content, member);
        }
}
