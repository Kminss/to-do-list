package com.sparta.todo.dto.response;

import com.sparta.todo.domain.ToDo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

public record CreateToDoResponse(
        @Schema(
                description = "할 일 ID",
                nullable = false,
                example = "1"
        )
        Long id,
        @Schema(
                description = "제목",
                nullable = false,
                example = "알고리즘 풀기"
        )
        String title,
        @Schema(
                description = "내용",
                nullable = false,
                example = "LV2 한 문제 풀기"
        )
        String content,
        @Schema(
                description = "로그인 아이디",
                nullable = false,
                example = "username1"
        )
        String username,
        @Schema(
                description = "작성일",
                nullable = false,
                example = "2023-11-18T01:26:23.982168"
        )
        LocalDateTime createdDateTime
) {
    @Builder
    public CreateToDoResponse {
    }

    public static CreateToDoResponse from(ToDo entity) {
        return CreateToDoResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .username(entity.getMember().getUsername())
                .createdDateTime(entity.getCreatedDateTime())
                .build();
    }
}
