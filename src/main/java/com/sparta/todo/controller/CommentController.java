package com.sparta.todo.controller;

import com.sparta.todo.annotaion.CurrentMember;
import com.sparta.todo.dto.MemberDto;
import com.sparta.todo.dto.request.CommentRequest;
import com.sparta.todo.dto.response.CommentResponse;
import com.sparta.todo.dto.response.ErrorResponse;
import com.sparta.todo.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "댓글 API", description = "댓글 API")
@RequestMapping("/api/v1/todos/{toDoId}/comments")
@RestController
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @Operation(summary = "댓글 생성", description = "댓글 생성 API")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "댓글 생성 성공",
                    content = @Content(schema = @Schema(implementation = CommentResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "댓글 달 할 일이 없는 경우",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping
    public ResponseEntity<CommentResponse> createComment(
            @Parameter(description = "할 일 ID")
            @PathVariable("toDoId") Long toDoId,
            @RequestBody CommentRequest request,
            @CurrentMember MemberDto memberDto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.createComment(toDoId, request, memberDto));
    }

    @Operation(summary = "댓글 수정", description = "댓글 수정 API")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "댓글 수정 성공",
                    content = @Content(schema = @Schema(implementation = CommentResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "댓글에 대해 수정 권한이 없는 경우",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "수정할 댓글이 없는 경우",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
            @Parameter(description = "할 일 ID")
            @PathVariable("toDoId") Long toDoId,
            @Parameter(description = "댓글 ID")
            @PathVariable("commentId") Long commentId,
            @RequestBody CommentRequest request,
            @CurrentMember MemberDto memberDto
    ) {
        return ResponseEntity.ok(commentService.updateComment(commentId, request, memberDto));
    }


    @Operation(summary = "댓글 삭제", description = "댓글 삭제 API")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "댓글 삭제 성공"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "댓글에 대해 삭제 권한이 없는 경우",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "삭제할 댓글이 없는 경우",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Object> deleteComment(
            @Parameter(description = "할 일 ID")
            @PathVariable("toDoId") Long toDoId,
            @Parameter(description = "댓글 ID")
            @PathVariable("commentId") Long commentId,
            @CurrentMember MemberDto memberDto
    ) {
        commentService.deleteComment(commentId, memberDto);
        return ResponseEntity.noContent().build();
    }
}


