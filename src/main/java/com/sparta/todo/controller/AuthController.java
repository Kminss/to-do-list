package com.sparta.todo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.todo.dto.response.ErrorResponse;
import com.sparta.todo.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Tag(name = "인증 API", description = "토큰재발급, 로그아웃 등 인증관련 API 컨트롤러")
@RequestMapping("/api/v1/auth")
@RestController
public class AuthController {
	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@Operation(summary = "토큰 재발급",
		description = "토큰 재발급 API",
		parameters = {@Parameter(name = "RefreshToken", description = "리프레쉬 토큰", in = ParameterIn.COOKIE)})
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "201",
			description = "재발급 성공",
			headers = {
				@Header(name = "Authorization", description = "엑세스 토큰", required = true),
				@Header(
					name = "Set-Cookie",
					description = "RefreshToken",
					schema =
					@Schema(type = "String", name = "RefreshToken", description = "리프레쉬 토큰")
				)
			}
		),
		@ApiResponse(
			responseCode = "400",
			description = "재발급 실패 - 유효하지 않은 토큰으로 요청 시 ",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class))
		),
	})
	@PostMapping("/reissue")
	public ResponseEntity<Object> reissue(HttpServletRequest request, HttpServletResponse response) {
		authService.reissue(request, response);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@Operation(summary = "로그아웃",
		description = "로그아웃 API",
		parameters = {@Parameter(name = "Authorization", description = "엑세스 토큰", in = ParameterIn.HEADER)})
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "204",
			description = "로그아웃 성공"
		),
		@ApiResponse(
			responseCode = "400",
			description = "로그아웃 실패 - 유효하지 않은 토큰으로 요청 시 ",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class))
		),
	})
	@DeleteMapping("/logout")
	public ResponseEntity<Object> logout(HttpServletRequest request) {
		authService.logout(request);
		return ResponseEntity.noContent().build();
	}
}
