package com.sparta.todo.dto.response;

import com.sparta.todo.domain.Comment;

import java.time.LocalDateTime;

public record CommentResponse(
        Long commentId,
        Long toDoId,
        String username,
        String content,
        LocalDateTime createdDateTime


) {
    public static CommentResponse from(Comment entity) {
        return new CommentResponse(
                entity.getId(),
                entity.getTodo().getId(),
                entity.getMember().getUsername(),
                entity.getContent(),
                entity.getCreatedDateTime()
        );
    }
}
