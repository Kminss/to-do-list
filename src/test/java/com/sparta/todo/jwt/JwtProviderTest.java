package com.sparta.todo.jwt;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;

import com.sparta.todo.domain.constant.MemberRole;
import com.sparta.todo.dto.response.TokenDto;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;

@DisplayName("Jwt 테스트")
@ExtendWith({MockitoExtension.class, OutputCaptureExtension.class})
class JwtProviderTest {
	private final Long accessTokenExpiration = 10000L;
	private final Long refreshTokenExpiration = 100000L;
	private final String secretKey = "kVh4tD6YvLv87AT2PWZ2jLBSdt4hNp9eyF4sPX1lSQo=";
	@InjectMocks
	JwtProvider jwtProvider;

	@BeforeEach
	void setUp() {
		ReflectionTestUtils.setField(jwtProvider, "accessTokenExpiration", accessTokenExpiration);
		ReflectionTestUtils.setField(jwtProvider, "refreshTokenExpiration", refreshTokenExpiration);
		ReflectionTestUtils.setField(jwtProvider, "secretKey", secretKey);
		jwtProvider.init();
	}

	@DisplayName("Jwt 토큰 생성 테스트")
	@Test
	void givenUsernameAndRole_whenCrateToken_thenReturnTokenDto() throws Exception {
		//Given
		String username = "testUsername";
		MemberRole role = MemberRole.USER;

		//When
		TokenDto dto = jwtProvider.createToken(username, role);

		//Then
		assertThat(dto.accessToken()).isNotNull();
		assertThat(dto.refreshToken()).isNotNull();
		assertThat(dto.accessToken()).startsWith("ey");
	}

	@DisplayName("Jwt 토큰 검증 테스트")
	@Test
	void givenJwt_whenValidate_thenReturnTrue() throws Exception {
		//Given
		String username = "testUsername";
		MemberRole role = MemberRole.USER;
		TokenDto dto = jwtProvider.createToken(username, role);

		//When && Then
		assertThat(jwtProvider.validateToken(dto.accessToken())).isTrue();
	}
	@DisplayName("Jwt 토큰 검증 시간 만료 테스트")
	@Test
	void givenExpireJwt_whenValidate_thenReturnFalse(CapturedOutput output) throws Exception {
		//Given
		String username = "testUsername";
		MemberRole role = MemberRole.USER;
		ReflectionTestUtils.setField(jwtProvider, "accessTokenExpiration", 300L);

		TokenDto dto = jwtProvider.createToken(username, role);

		//When && Then
		Thread.sleep(300L);
		assertThat(jwtProvider.validateToken(dto.accessToken())).isFalse();
		assertThat(output.getOut()).contains("만료");
	}

	@DisplayName("Jwt 토큰 검증 잘못된 토큰 테스트")
	@Test
	void givenInvalidJwt_whenValidate_thenReturnFalse(CapturedOutput output) throws Exception {
		//Given
		String username = "testUsername";
		MemberRole role = MemberRole.USER;
		ReflectionTestUtils.setField(jwtProvider, "accessTokenExpiration", accessTokenExpiration);

		TokenDto dto = jwtProvider.createToken(username, role);

		//When && Then
		assertThat(jwtProvider.validateToken("")).isFalse();
		assertThat(output.getOut()).contains("잘못된 JWT 토큰");
	}

	@DisplayName("Jwt 토큰 검증 유효하지 않은 서명 테스트")
	@Test
	void givenInvalidSignatureJwt_whenValidate_thenReturnFalse(CapturedOutput output) throws Exception {
		//Given
		String username = "testUsername";
		MemberRole role = MemberRole.USER;

		TokenDto dto = jwtProvider.createToken(username, role);

		//When && Then
		assertThat(jwtProvider.validateToken(dto.accessToken()+"asdlkfjdasklf")).isFalse();
		assertThat(output.getOut()).contains("유효하지 않는 JWT 서명");
	}

	@DisplayName("Jwt Bearer 토큰 SubString 테스트")
	@Test
	void givenJwtBearer_whenSubstringToken_thenReturnJwt() throws Exception {
		//Given
		String username = "testUsername";
		MemberRole role = MemberRole.USER;

		TokenDto dto = jwtProvider.createToken(username, role);

		//When
		String subToken = jwtProvider.substringToken("Bearer " + dto.accessToken());
		//Then
		assertThat(dto.accessToken()).isEqualTo(subToken);
	}

	@DisplayName("Access Jwt Header 추출 테스트")
	@Test
	void givenHttpRequest_whenExtractJwtToRequestHeader_thenReturnJwt() throws Exception {
		//Given
		MockHttpServletRequest request = new MockHttpServletRequest();

		String username = "testUsername";
		MemberRole role = MemberRole.USER;
		TokenDto dto = jwtProvider.createToken(username, role);
		request.addHeader("Authorization", "Bearer " + dto.accessToken());

		//When
		String extractToken = jwtProvider.getTokenFromRequestHeader(request);
		//Then
		assertThat(extractToken).isEqualTo(dto.accessToken());
	}

	@DisplayName("Refresh Jwt Cookie 추출 테스트")
	@Test
	void givenHttpRequest_whenExtractJwtToRequestCookie_thenReturnJwt() throws Exception {
		//Given
		MockHttpServletRequest request = new MockHttpServletRequest();

		String username = "testUsername";
		MemberRole role = MemberRole.USER;
		TokenDto dto = jwtProvider.createToken(username, role);
		request.setCookies(new Cookie("RefreshToken", "Bearer " + dto.refreshToken()));

		//When
		String extractToken = jwtProvider.getRefreshTokenFromCookie(request);
		//Then
		assertThat(extractToken).isEqualTo(dto.refreshToken());
	}
	@DisplayName("Jwt response 삽입 테스트")
	@Test
	void givenJwtBearer_whenSetJwtToResponse_thenSetJwt() throws Exception {
		//Given
		MockHttpServletResponse response = new MockHttpServletResponse();

		String username = "testUsername";
		MemberRole role = MemberRole.USER;
		TokenDto dto = jwtProvider.createToken(username, role);

		//When
		jwtProvider.setTokenResponse(dto, response);
		//Then
		assertThat(response.getHeader("Authorization")).isEqualTo("Bearer " + dto.accessToken());

	}

	@DisplayName("Jwt 사용자 정보 추출 테스트")
	@Test
	void givenJwtBearer_whenExtractUserInfo_thenReturnClaims() throws Exception {
		//Given
		MockHttpServletResponse response = new MockHttpServletResponse();

		String username = "testUsername";
		MemberRole role = MemberRole.USER;
		TokenDto dto = jwtProvider.createToken(username, role);

		//When
		Claims claims = jwtProvider.getUserInfoFromToken(dto.accessToken());
		//Then
		assertThat(claims.getSubject()).isEqualTo(username);
	}
}