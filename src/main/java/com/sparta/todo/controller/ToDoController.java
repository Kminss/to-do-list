package com.sparta.todo.controller;

import com.sparta.todo.annotaion.CurrentMember;
import com.sparta.todo.domain.constant.SearchType;
import com.sparta.todo.dto.MemberDto;
import com.sparta.todo.dto.request.CreateToDoRequest;
import com.sparta.todo.dto.request.SearchToDoCondition;
import com.sparta.todo.dto.request.UpdateToDoRequest;
import com.sparta.todo.dto.response.ToDoResponse;
import com.sparta.todo.dto.response.ErrorResponse;
import com.sparta.todo.service.ToDoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
                    content = @Content(schema = @Schema(implementation = ToDoResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "회원 정보가 없는 경우",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping("/todos")
    public ResponseEntity<ToDoResponse> createToDo(@RequestBody CreateToDoRequest request, @CurrentMember MemberDto memberDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(toDoService.createToDo(request, memberDto));
    }

    @Operation(summary = "할 일 조회", description = "할 일 조회 API")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "할 일 조회 성공",
                    content = @Content(schema = @Schema(implementation = ToDoResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "조회할 할 일이 없는 경우",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/todos/{toDoId}")
    public ResponseEntity<ToDoResponse> getToDo(
            @Parameter(description = "할 일 ID", in = ParameterIn.PATH)
            @PathVariable(value = "toDoId") Long toDoId
            ) {
        return ResponseEntity.ok(toDoService.getToDo(toDoId));
    }

    @Operation(summary = "할 일 검색", description = "할 일 검색 API")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "할 일 검색 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ToDoResponse.class)))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "검색할 할 할 일이 없는 경우",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/todos")
    public ResponseEntity<List<ToDoResponse>> searchToDos(
            @Parameter(name = "keyword", description = "검색어")
            @RequestParam(name = "keyword", required = false) String keyword,

            @Parameter(name = "searchType", description = "검색 종류", required = true)
            @RequestParam(name = "searchType") String searchType
    ) {
        SearchType type = SearchType.findMatchedEnum(searchType);
        SearchToDoCondition condition = SearchToDoCondition.of(type, keyword);

        return ResponseEntity.ok(toDoService.searchToDos(condition));
    }

    @Operation(summary = "할 일 수정", description = "할 일 수정 API")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "할 일 수정 성공",
                    content = @Content(schema = @Schema(implementation = ToDoResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "수정할 할 일이 없는 경우",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "할 일에 대해 수정 권한이 없는 경우",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PutMapping("/todos/{toDoId}")
    public ResponseEntity<ToDoResponse> updateToDo(
            @Parameter(description = "할 일 ID", in = ParameterIn.PATH)
            @PathVariable(value = "toDoId") Long toDoId,
            @RequestBody UpdateToDoRequest request,
            @CurrentMember MemberDto memberDto
    ) {
        return ResponseEntity.ok(toDoService.updateToDo(toDoId, request, memberDto));
    }

    @Operation(summary = "할 일 삭제", description = "할 일 삭제 API")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "할 일 삭제 성공"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "삭제할 할 일이 없는 경우",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "할 일에 대해 삭제 권한이 없는 경우",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @DeleteMapping("/todos/{toDoId}")
    public ResponseEntity<Object> deleteToDo(
            @Parameter(description = "할 일 ID", in = ParameterIn.PATH)
            @PathVariable(value = "toDoId") Long toDoId,
            @CurrentMember MemberDto memberDto
    ) {
        toDoService.deleteToDo(toDoId, memberDto);

        return ResponseEntity.noContent().build();
    }

}
