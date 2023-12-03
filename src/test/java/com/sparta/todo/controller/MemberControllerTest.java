package com.sparta.todo.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.security.Principal;

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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.todo.config.PasswordEncoderConfig;
import com.sparta.todo.config.SecurityConfig;
import com.sparta.todo.domain.constant.MemberRole;
import com.sparta.todo.dto.MemberDto;
import com.sparta.todo.dto.request.SignupRequest;
import com.sparta.todo.exception.AlreadyHiddenToDoException;
import com.sparta.todo.security.CustomUserDetails;
import com.sparta.todo.security.MockSecurityFilter;
import com.sparta.todo.service.MemberService;

@Import(PasswordEncoderConfig.class)
@WebMvcTest(controllers = MemberController.class,
	excludeFilters = {
		@ComponentScan.Filter(
			type = FilterType.ASSIGNABLE_TYPE,
			classes = SecurityConfig.class
		)
	})
@DisplayName("회원 API 컨트롤러 테스트")
class MemberControllerTest {

	private MockMvc mvc;
	private Principal mockPrincipal;
	@Autowired
	private WebApplicationContext context;
	@Autowired
	private ObjectMapper objectMapper;
	@MockBean
	private MemberService memberService;


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

	@DisplayName("[Controller][POST] 회원가입 요청 성공")
	@Test
	void givenSignupInfo_whenRequesting_thenReturn201 () throws Exception {

		//Given
		this.mockUserSetup();
		SignupRequest request = new SignupRequest("username", "testpassword");

	    //When
		ResultActions actions = mvc.perform(
			post("/api/v1/member/signup")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.principal(mockPrincipal)
		);
	    
	    //Then
		verify(memberService, times(1)).signup(request);

		actions
			.andDo(print())
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.message").value("회원가입에 성공하였습니다."));
	}

	@DisplayName("[Controller][POST] 회원가입 요청 실패 - 이미 아이디가 존재하는 경우")
	@Test
	void givenSignupInfo_whenRequesting_thenReturn409 () throws Exception {

		//Given
		this.mockUserSetup();
		SignupRequest request = new SignupRequest("username", "testpassword");
		willThrow(new AlreadyHiddenToDoException()).given(memberService).signup(request);

		//When

		ResultActions actions = mvc.perform(
			post("/api/v1/member/signup")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.principal(mockPrincipal)
		);

		//Then
		verify(memberService, times(1)).signup(request);

		actions
			.andDo(print())
			.andExpect(status().isConflict());
	}

	@DisplayName("[Controller][POST] 회원가입 요청 실패 - 요청 데이터 유효성 검증 실패")
	@Test
	void givenSignupInfo_whenRequesting_thenReturn400 () throws Exception {

		//Given
		this.mockUserSetup();
		SignupRequest request = new SignupRequest("", "");

		//When
		ResultActions actions = mvc.perform(
			post("/api/v1/member/signup")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.principal(mockPrincipal)
		);

		//Then
		actions
			.andDo(print())
			.andExpect(status().isBadRequest());
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