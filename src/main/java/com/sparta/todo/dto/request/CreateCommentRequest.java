package com.sparta.todo.dto.request;

import com.sparta.todo.domain.Comment;
import com.sparta.todo.domain.Member;
import com.sparta.todo.domain.ToDo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(description = "댓글 생성 요청")
public record CreateCommentRequest(
        @Schema(description = "댓글 내용", nullable = false, example = "안녕하세요")
        @Size(max = 500, message = "최대 500자까지 작성가능합니다.")
        String content
) {

    public Comment toEntity(Member member, ToDo toDo) {
        return Comment.of(member, toDo, content);
    }
}
