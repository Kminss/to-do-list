package com.sparta.todo.service;

import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;

import com.sparta.todo.domain.constant.MemberRole;
import com.sparta.todo.dto.response.TokenDto;
import com.sparta.todo.jwt.JwtProvider;
import com.sparta.todo.util.RedisUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@DisplayName("서비스 테스트 - Auth")
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
	@InjectMocks
	private AuthService sut;

	@Mock
	private RedisUtils redisUtils;

	@Mock
	JwtProvider jwtProvider;

	private final Long accessTokenExpiration = 10000L;
	private final Long refreshTokenExpiration = 100000L;
	private final String secretKey = "kVh4tD6YvLv87AT2PWZ2jLBSdt4hNp9eyF4sPX1lSQo=";

	@BeforeEach
	void setUp() {
		ReflectionTestUtils.setField(jwtProvider, "accessTokenExpiration", accessTokenExpiration);
		ReflectionTestUtils.setField(jwtProvider, "refreshTokenExpiration", refreshTokenExpiration);
		ReflectionTestUtils.setField(jwtProvider, "secretKey", secretKey);
		jwtProvider.init();
	}

	@DisplayName("리프래쉬 토큰으로 재발급 요청시 재발급")
	@Test
	void givenHttpRequestAndResponse_whenReissueToken_thenReissue() throws Exception {
		TokenDto dto = new TokenDto("testAccessToken", "testRefreshToken");
		System.out.println(dto.refreshToken());

		//Given
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		given(jwtProvider.getRefreshTokenFromCookie(any())).willReturn(dto.refreshToken());
		given(jwtProvider.validateToken(any())).willReturn(true);


		Claims claims= Jwts.claims().setSubject("username");
		claims.put("auth", MemberRole.USER.name());

		given(jwtProvider.getUserInfoFromToken(any())).willReturn(claims);
		given(redisUtils.getKey(any())).willReturn(dto.refreshToken());

		given(jwtProvider.createToken(any(), any())).willReturn(dto);

		//When
		sut.reissue(request, response);

		//Then
		then(jwtProvider).should().setTokenResponse(any(), any());
		then(redisUtils).should().saveKey(any(), any(), any());
	}

	@DisplayName("로그아웃 테스트")
	@Test
	void givenHttpRequest_whenLogout_thenAddBlackList() throws Exception {
		String username = "testUsername";
		MemberRole role = MemberRole.USER;
		TokenDto dto = new TokenDto("testAccessToken", "testRefreshToken");
		System.out.println(dto.refreshToken());

		//Given
		MockHttpServletRequest request = new MockHttpServletRequest();
		given(jwtProvider.getTokenFromRequestHeader(any())).willReturn(dto.accessToken());
		given(jwtProvider.validateToken(any())).willReturn(true);


		Claims claims= Jwts.claims().setSubject("username");
		claims.put("auth", MemberRole.USER.name());
		given(jwtProvider.getUserInfoFromToken(any())).willReturn(claims);

		//When
		sut.logout(request);

		//Then
		then(redisUtils).should().deleteKey(any(String.class));
		then(redisUtils).should().saveKey(any(), any(), any());
	}
}