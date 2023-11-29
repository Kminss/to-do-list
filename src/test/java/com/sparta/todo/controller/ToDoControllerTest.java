package com.sparta.todo.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.todo.config.PasswordEncoderConfig;
import com.sparta.todo.config.SecurityConfig;
import com.sparta.todo.domain.ToDo;
import com.sparta.todo.domain.constant.MemberRole;
import com.sparta.todo.dto.MemberDto;
import com.sparta.todo.dto.request.CreateToDoRequest;
import com.sparta.todo.dto.request.SearchToDoCondition;
import com.sparta.todo.dto.request.UpdateToDoRequest;
import com.sparta.todo.dto.response.ToDoResponse;
import com.sparta.todo.exception.AccessDeniedException;
import com.sparta.todo.exception.AlreadyCompleteToDoException;
import com.sparta.todo.exception.AlreadyHiddenToDoException;
import com.sparta.todo.exception.ErrorCode;
import com.sparta.todo.exception.MemberNotFoundException;
import com.sparta.todo.exception.NotCompleteToDoException;
import com.sparta.todo.exception.ToDoNotFoundException;
import com.sparta.todo.security.CustomUserDetails;
import com.sparta.todo.security.MockSecurityFilter;
import com.sparta.todo.service.ToDoService;

@Import(PasswordEncoderConfig.class)
@WebMvcTest(controllers = ToDoController.class,
	excludeFilters = {
		@ComponentScan.Filter(
			type = FilterType.ASSIGNABLE_TYPE,
			classes = SecurityConfig.class
		)
	})
@DisplayName("할 일 API 컨트롤러 테스트")
class ToDoControllerTest {

	private MockMvc mvc;
	private Principal mockPrincipal;
	@Autowired
	private WebApplicationContext context;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private PasswordEncoder passwordEncoder;

	@MockBean
	private ToDoService toDoService;

	@BeforeEach
	public void setUp() {
		mvc = MockMvcBuilders
			.webAppContextSetup(context)
			.apply(springSecurity(new MockSecurityFilter()))
			.build();
	}

	private void mockUserSetup() {
		// Mock 테스트 유져 생성
		CustomUserDetails testUserDetails = new CustomUserDetails(getMemberDto());
		mockPrincipal = new UsernamePasswordAuthenticationToken(testUserDetails, "", testUserDetails.getAuthorities());
	}

	@DisplayName("[Controller][POST] 할일 생성 요청 성공")
	@Test
	void givenToDoRequestAndMemberInfo_whenRequesting_thenSuccess() throws Exception {
		//Given
		this.mockUserSetup();
		MemberDto memberDto = getMemberDto();
		CreateToDoRequest toDoRequest = new CreateToDoRequest("test title", "test content");

		given(toDoService.createToDo(any(), any(MemberDto.class))).willReturn(
			ToDoResponse.from(new ToDo(
				toDoRequest.title(),
				toDoRequest.content(),
				memberDto.toEntity(passwordEncoder)
			))
		);

		//When
		ResultActions actions = mvc.perform(
			post("/api/v1/todos")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(toDoRequest))
				.accept(MediaType.APPLICATION_JSON)
				.principal(mockPrincipal)

		);

		//Then
		actions
			.andDo(print())
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.title").value(toDoRequest.title()))
			.andExpect(jsonPath("$.content").value(toDoRequest.content()))
			.andExpect(jsonPath("$.username").value(mockPrincipal.getName()));
	}

	@DisplayName("[Controller][POST] 할일 생성 요청 실패 - 회원 정보가 없는 경우")
	@Test
	void givenToDoRequestAndMemberInfo_whenRequesting_thenReturn404() throws Exception {
		//Given
		this.mockUserSetup();
		MemberDto memberDto = getMemberDto();
		CreateToDoRequest toDoRequest = new CreateToDoRequest("test title", "test content");

		given(toDoService.createToDo(toDoRequest, memberDto))
			.willThrow(new MemberNotFoundException());

		//When

		ResultActions actions = mvc.perform(
			post("/api/v1/todos")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(toDoRequest))
				.accept(MediaType.APPLICATION_JSON)
				.principal(mockPrincipal)
		);

		//Then
		actions
			.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.name").value(ErrorCode.MEMBER_NOT_FOUND.name()))
			.andExpect(jsonPath("$.message").value(ErrorCode.MEMBER_NOT_FOUND.getDetail()));
	}

	@DisplayName("[Controller][GET] 할일 검색 요청 성공")
	@Test
	void givenSearchToDoId_whenRequesting_thenReturnToDoInfos() throws Exception {
		//Given
		this.mockUserSetup();
		given(toDoService.searchToDos(any(SearchToDoCondition.class), any(MemberDto.class)))
			.willReturn(List.of());

		//When
		ResultActions actions = mvc.perform(
			get("/api/v1/todos")
				.param("searchType", "TITLE")
				.accept(MediaType.APPLICATION_JSON)
				.principal(mockPrincipal)
		);

		//Then
		then(toDoService).should().searchToDos(any(SearchToDoCondition.class), any(MemberDto.class));

		actions
			.andDo(print())
			.andExpect(status().isOk());
	}

	@DisplayName("[Controller][GET] 할일 검색 요청 실패 - 할 일이 없는 경우")
	@Test
	void givenSearchToDoId_whenRequesting_thenReturn404() throws Exception {
		//Given
		this.mockUserSetup();
		given(toDoService.searchToDos(any(SearchToDoCondition.class), any(MemberDto.class)))
			.willThrow(new ToDoNotFoundException());
		//When
		ResultActions actions = mvc.perform(
			get("/api/v1/todos")
				.param("searchType", "TITLE")
				.accept(MediaType.APPLICATION_JSON)
				.principal(mockPrincipal)
		);

		//Then
		then(toDoService).should().searchToDos(any(SearchToDoCondition.class), any(MemberDto.class));

		actions
			.andDo(print())
			.andExpect(status().isNotFound());
	}

	@DisplayName("[Controller][GET] 할일 조회 요청 성공")
	@Test
	void givenToDoId_whenRequesting_thenReturnToDoInfo() throws Exception {
		//Given
		this.mockUserSetup();

		Long toDoId = 1L;
		ToDoResponse response = new ToDoResponse(
			toDoId,
			"test title",
			"testContent",
			mockPrincipal.getName(),
			null,
			false,
			LocalDateTime.now()
		);

		given(toDoService.getToDo(toDoId)).willReturn(response);

		//When
		ResultActions actions = mvc.perform(
			get("/api/v1/todos/" + toDoId)
				.accept(MediaType.APPLICATION_JSON)
				.principal(mockPrincipal)
		);

		//Then
		actions
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(response.id()))
			.andExpect(jsonPath("$.title").value(response.title()))
			.andExpect(jsonPath("$.content").value(response.content()))
			.andExpect(jsonPath("$.username").value(mockPrincipal.getName()));
	}

	@DisplayName("[Controller][GET] 할일 조회 요청 실패 - 조회 대상이 없는경우")
	@Test
	void givenToDoId_whenRequesting_thenThrowToDoNotFoundException() throws Exception {
		//Given
		this.mockUserSetup();
		Long toDoId = 1L;

		given(toDoService.getToDo(toDoId)).willThrow(new ToDoNotFoundException());

		//When
		ResultActions actions = mvc.perform(
			get("/api/v1/todos/" + toDoId)
				.principal(mockPrincipal)
		);

		//Then
		actions
			.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.name").value(ErrorCode.TO_DO_NOT_FOUND.name()))
			.andExpect(jsonPath("$.message").value(ErrorCode.TO_DO_NOT_FOUND.getDetail()));
	}

	@DisplayName("[Controller][PUT] 할일 수정 요청 성공")
	@Test
	void givenUpdateToDoInfo_whenRequesting_thenReturnUpdateToDoInfo() throws Exception {
		//Given
		this.mockUserSetup();
		Long toDoId = 1L;
		UpdateToDoRequest toDoRequest = new UpdateToDoRequest("update test title", "update test content");

		ToDoResponse response = new ToDoResponse(
			toDoId,
			toDoRequest.title(),
			toDoRequest.content(),
			mockPrincipal.getName(),
			null,
			false,
			LocalDateTime.now()
		);
		given(toDoService.updateToDo(toDoId, toDoRequest, getMemberDto())).willReturn(response);

		//When
		ResultActions actions = mvc.perform(
			put("/api/v1/todos/" + toDoId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(toDoRequest))
				.accept(MediaType.APPLICATION_JSON)
				.principal(mockPrincipal)
		);

		//Then
		actions
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.title").value(toDoRequest.title()))
			.andExpect(jsonPath("$.content").value(toDoRequest.content()))
			.andExpect(jsonPath("$.username").value(mockPrincipal.getName()));
	}

	@DisplayName("[Controller][PUT] 할일 수정 요청 실패 - 수정 권한 없는 경우")
	@Test
	void givenUpdateToDoInfo_whenRequesting_thenThrowAccessDeniedException() throws Exception {
		//Given
		this.mockUserSetup();
		Long toDoId = 1L;
		UpdateToDoRequest toDoRequest = new UpdateToDoRequest("update test title", "update test content");

		given(toDoService.updateToDo(toDoId, toDoRequest, getMemberDto()))
			.willThrow(new AccessDeniedException("해당 할 일에 대한 권한이 없습니다."));

		//When
		ResultActions actions = mvc.perform(
			put("/api/v1/todos/" + toDoId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(toDoRequest))
				.accept(MediaType.APPLICATION_JSON)
				.principal(mockPrincipal)
		);

		//Then
		actions
			.andDo(print())
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.name").value(ErrorCode.ACCESS_DENIED.name()))
			.andExpect(jsonPath("$.message").value("해당 할 일에 대한 권한이 없습니다."));
	}

	@DisplayName("[Controller][PUT] 할일 수정 요청 실패 - 수정할 할일이 없는 경우")
	@Test
	void givenUpdateToDoInfo_whenRequesting_thenThrowToDoNotFoundException() throws Exception {
		//Given
		this.mockUserSetup();
		Long toDoId = 1L;
		UpdateToDoRequest toDoRequest = new UpdateToDoRequest("update test title", "update test content");

		given(toDoService.updateToDo(toDoId, toDoRequest, getMemberDto()))
			.willThrow(new ToDoNotFoundException());

		//When
		ResultActions actions = mvc.perform(
			put("/api/v1/todos/" + toDoId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(toDoRequest))
				.accept(MediaType.APPLICATION_JSON)
				.principal(mockPrincipal)
		);

		//Then
		actions
			.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.name").value(ErrorCode.TO_DO_NOT_FOUND.name()))
			.andExpect(jsonPath("$.message").value(ErrorCode.TO_DO_NOT_FOUND.getDetail()));
	}


	@DisplayName("[Controller][DELETE] 할일 삭제 요청 성공")
	@Test
	void givenDeleteToDoId_whenRequesting_thenReturn204() throws Exception {
		//Given
		this.mockUserSetup();
		Long toDoId = 1L;

		willDoNothing().given(toDoService).deleteToDo(toDoId, getMemberDto());

		//When
		ResultActions actions = mvc.perform(
			delete("/api/v1/todos/" + toDoId)
				.principal(mockPrincipal)
		);

		//Then
		actions
			.andDo(print())
			.andExpect(status().isNoContent());
	}

	@DisplayName("[Controller][DELETE] 할일 삭제 요청 실패 - 삭제할 할일이 없는 경우")
	@Test
	void givenDeleteToDoId_whenRequesting_thenThrowToDoNotFoundException() throws Exception {
		//Given
		this.mockUserSetup();
		Long toDoId = 1L;

		willThrow(new ToDoNotFoundException()).given(toDoService).deleteToDo(toDoId, getMemberDto());

		//When
		ResultActions actions = mvc.perform(
			delete("/api/v1/todos/" + toDoId)
				.principal(mockPrincipal)
		);

		//Then
		actions
			.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.name").value(ErrorCode.TO_DO_NOT_FOUND.name()))
			.andExpect(jsonPath("$.message").value(ErrorCode.TO_DO_NOT_FOUND.getDetail()));
	}

	@DisplayName("[Controller][DELETE] 할일 삭제 요청 실패 - 삭제할 권한이 없는 경우")
	@Test
	void givenDeleteToDoId_whenRequesting_thenThrowAccessDeniedException() throws Exception {
		//Given
		this.mockUserSetup();
		Long toDoId = 1L;

		willThrow(new AccessDeniedException("해당 할 일에 대한 권한이 없습니다."))
			.given(toDoService).deleteToDo(toDoId, getMemberDto());

		//When
		ResultActions actions = mvc.perform(
			delete("/api/v1/todos/" + toDoId)
				.principal(mockPrincipal)
		);

		//Then
		actions
			.andDo(print())
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.name").value(ErrorCode.ACCESS_DENIED.name()))
			.andExpect(jsonPath("$.message").value("해당 할 일에 대한 권한이 없습니다."));
	}


	@DisplayName("[Controller][POST] 할일 완료 요청 성공")
	@Test
	void givenCompleteToDoId_whenRequesting_thenReturn204() throws Exception {
		//Given
		this.mockUserSetup();
		Long toDoId = 1L;

		willDoNothing().given(toDoService).completeToDo(toDoId, getMemberDto());

		//When
		ResultActions actions = mvc.perform(
			post("/api/v1/todos/" + toDoId + "/complete")
				.principal(mockPrincipal)
		);

		//Then
		actions
			.andDo(print())
			.andExpect(status().isNoContent());
	}
	@DisplayName("[Controller][POST] 할일 완료 요청 실패 - 이미 완료처리된 경우")
	@Test
	void givenCompleteToDoId_whenRequesting_thenReturn409() throws Exception {
		//Given
		this.mockUserSetup();
		Long toDoId = 1L;

		willThrow(new AlreadyCompleteToDoException()).given(toDoService).completeToDo(toDoId, getMemberDto());

		//When
		ResultActions actions = mvc.perform(
			post("/api/v1/todos/" + toDoId + "/complete")
				.principal(mockPrincipal)
		);

		//Then
		actions
			.andDo(print())
			.andExpect(status().isConflict());
	}

	@DisplayName("[Controller][DELETE] 할일 완료 취소 요청 성공")
	@Test
	void givenCancelCompleteToDoId_whenRequesting_thenReturn204() throws Exception {
		//Given
		this.mockUserSetup();
		Long toDoId = 1L;

		willDoNothing().given(toDoService).deleteCompleteToDo(toDoId, getMemberDto());

		//When
		ResultActions actions = mvc.perform(
			delete("/api/v1/todos/" + toDoId + "/complete")
				.principal(mockPrincipal)
		);

		//Then
		actions
			.andDo(print())
			.andExpect(status().isNoContent());
	}

	@DisplayName("[Controller][DELETE] 할일 완료 취소 요청 실패 - 완료되어있지 않은 경우")
	@Test
	void givenCancelCompleteToDoId_whenRequesting_thenReturn404() throws Exception {
		//Given
		this.mockUserSetup();
		Long toDoId = 1L;

		willThrow(new NotCompleteToDoException()).given(toDoService).deleteCompleteToDo(toDoId, getMemberDto());

		//When
		ResultActions actions = mvc.perform(
			delete("/api/v1/todos/" + toDoId + "/complete")
				.principal(mockPrincipal)
		);

		//Then
		actions
			.andDo(print())
			.andExpect(status().isNotFound());
	}

	@DisplayName("[Controller][POST] 할일 비공개 요청 성공")
	@Test
	void givenHiddenToDoId_whenRequesting_thenReturn204() throws Exception {
		//Given
		this.mockUserSetup();
		Long toDoId = 1L;

		willDoNothing().given(toDoService).completeToDo(toDoId, getMemberDto());

		//When
		ResultActions actions = mvc.perform(
			post("/api/v1/todos/" + toDoId + "/hidden")
				.principal(mockPrincipal)
		);

		//Then
		actions
			.andDo(print())
			.andExpect(status().isNoContent());
	}
	@DisplayName("[Controller][POST] 할일 비공개 요청 실패 - 이미 비공개 처리된 경우")
	@Test
	void givenHiddenToDoId_whenRequesting_thenReturn409() throws Exception {
		//Given
		this.mockUserSetup();
		Long toDoId = 1L;

		willThrow(new AlreadyHiddenToDoException()).given(toDoService).hiddenToDo(toDoId, getMemberDto());

		//When
		ResultActions actions = mvc.perform(
			post("/api/v1/todos/" + toDoId + "/hidden")
				.principal(mockPrincipal)
		);

		//Then
		actions
			.andDo(print())
			.andExpect(status().isConflict());
	}

	@DisplayName("[Controller][DELETE] 할일 비공개 취소 요청 성공")
	@Test
	void givenCancelHiddenToDoId_whenRequesting_thenReturn204() throws Exception {
		//Given
		this.mockUserSetup();
		Long toDoId = 1L;

		willDoNothing().given(toDoService).deleteCompleteToDo(toDoId, getMemberDto());

		//When
		ResultActions actions = mvc.perform(
			delete("/api/v1/todos/" + toDoId + "/hidden")
				.principal(mockPrincipal)
		);

		//Then
		actions
			.andDo(print())
			.andExpect(status().isNoContent());
	}

	@DisplayName("[Controller][DELETE] 할일 비공개 취소 요청 실패 - 비공개 되어있지 않은 경우")
	@Test
	void givenCancelHiddenToDoId_whenRequesting_thenReturn404() throws Exception {
		//Given
		this.mockUserSetup();
		Long toDoId = 1L;

		willThrow(new NotCompleteToDoException()).given(toDoService).cancelHiddenToDo(toDoId, getMemberDto());

		//When
		ResultActions actions = mvc.perform(
			delete("/api/v1/todos/" + toDoId + "/hidden")
				.principal(mockPrincipal)
		);

		//Then
		actions
			.andDo(print())
			.andExpect(status().isNotFound());
	}

	private MemberDto getMemberDto() {
		String username = "minss1";
		String password = "minss1";
		MemberRole role = MemberRole.USER;
		return MemberDto.builder()
			.username(username)
			.password(password)
			.role(role)
			.build();
	}
}