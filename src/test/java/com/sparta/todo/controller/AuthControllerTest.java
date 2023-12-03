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
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.sparta.todo.config.SecurityConfig;
import com.sparta.todo.domain.constant.MemberRole;
import com.sparta.todo.dto.MemberDto;
import com.sparta.todo.dto.response.TokenDto;
import com.sparta.todo.jwt.JwtProvider;
import com.sparta.todo.security.CustomUserDetails;
import com.sparta.todo.security.MockSecurityFilter;
import com.sparta.todo.service.AuthService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Import({JwtProvider.class})
@EnableConfigurationProperties
@WebMvcTest(controllers = AuthController.class,
	excludeFilters = {
		@ComponentScan.Filter(
			type = FilterType.ASSIGNABLE_TYPE,
			classes = SecurityConfig.class
		)
	})
@DisplayName("인증 API 컨트롤러 테스트")
class AuthControllerTest {
	private MockMvc mvc;
	private Principal mockPrincipal;

	@Autowired
	private JwtProvider jwtProvider;
	@Autowired
	private WebApplicationContext context;

	@MockBean
	private AuthService authService;


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

	@DisplayName("[Controller][POST] 토큰 재발급 요청 성공")
	@Test
	void givenRefreshTokenCookie_whenRequesting_thenReturn201() throws Exception {

		//Given
		this.mockUserSetup();
		TokenDto tokenDto = jwtProvider.createToken(mockPrincipal.getName(), MemberRole.USER);

		MockHttpServletResponse response = new MockHttpServletResponse();

		Cookie cookie = new Cookie("RefreshToken", tokenDto.refreshToken()); // TODO: 12/3/23 물어보기 -> Cookie 세팅이안됨
		response.addCookie(cookie);

		//When
		ResultActions actions = mvc.perform(
			post("/api/v1/auth/reissue")
				.contentType(MediaType.APPLICATION_JSON)
				.cookie(cookie)
				.principal(mockPrincipal)
		);


		//Then
		verify(authService, times(1)).reissue(any(HttpServletRequest.class), any(HttpServletResponse.class));

		actions
			.andDo(print())
			.andExpect(status().isCreated());
	}


	@DisplayName("[Controller][DELETE] 로그아웃 요청 성공")
	@Test
	void givenAuthorizationToken_whenRequesting_thenReturn204() throws Exception {

		//Given
		this.mockUserSetup();
		TokenDto tokenDto = jwtProvider.createToken(mockPrincipal.getName(), MemberRole.USER);

		//When
		ResultActions actions = mvc.perform(
			delete("/api/v1/auth/logout")
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", tokenDto.accessToken())
				.principal(mockPrincipal)
		);


		//Then
		verify(authService, times(1)).logout(any(HttpServletRequest.class));

		actions
			.andDo(print())
			.andExpect(status().isNoContent());
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