package com.sparta.todo.controller;

import com.sparta.todo.annotaion.CurrentMember;
import com.sparta.todo.domain.constant.SearchType;
import com.sparta.todo.dto.MemberDto;
import com.sparta.todo.dto.request.CreateToDoRequest;
import com.sparta.todo.dto.request.SearchToDoCondition;
import com.sparta.todo.dto.request.UpdateToDoRequest;
import com.sparta.todo.dto.response.CreateToDoResponse;
import com.sparta.todo.dto.response.ErrorResponse;
import com.sparta.todo.dto.response.UpdateToDoResponse;
import com.sparta.todo.service.ToDoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "할 일 API", description = "할 일 API")
@RequestMapping("/api/v1")
@RestController
public class ToDoController {
    private final ToDoService toDoService;

    public ToDoController(ToDoService toDoService) {
        this.toDoService = toDoService;
    }

    @Operation(summary = "할 일 생성", description = "할 일 생성 API")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "할 일 생성 성공",
                    content = @Content(schema = @Schema(implementation = CreateToDoResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "회원 정보가 없는 경우",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping("/todos")
    public ResponseEntity<CreateToDoResponse> createToDo(@RequestBody CreateToDoRequest request, @CurrentMember MemberDto memberDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(toDoService.createToDo(request, memberDto));
    }
}