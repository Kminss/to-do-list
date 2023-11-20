package com.sparta.todo.dto.response;

import com.sparta.todo.domain.ToDo;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

public record ToDoResponse(
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

        @ArraySchema(schema = @Schema(implementation = CommentResponse.class))
        Set<CommentResponse> comments,

        @Schema(
                description = "완료여부",
                nullable = false,
                example = "false",
                allowableValues = {"true", "false"}
        )
        Boolean isDone,
        @Schema(
                description = "작성일",
                nullable = false,
                example = "2023-11-18T01:26:23.982168"
        )
        LocalDateTime createdDateTime

) {
    @Builder
    public ToDoResponse {
    }

    public static ToDoResponse from(ToDo entity) {
        return ToDoResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .username(entity.getMember().getUsername())
                .comments(entity.getComments().stream()
                        .map(CommentResponse::from)
                        .collect(Collectors.toUnmodifiableSet()))
                .isDone(entity.isDone())
                .createdDateTime(entity.getCreatedDateTime())
                .build();
    }
}
