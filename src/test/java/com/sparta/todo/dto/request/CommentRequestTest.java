package com.sparta.todo.dto.request;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@DisplayName("댓글 요청 유효성 검증")
class CommentRequestTest {
	private static ValidatorFactory factory;
	private static Validator validator;

	@BeforeAll
	public static void init() {
		factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	@AfterAll
	public static void close() {
		factory.close();
	}

	@DisplayName("댓글 내용 유효성 검증")
	@Nested
	class ContentTest {
		@DisplayName("댓글 내용이 빈값 또는 널인 경우 유효성 검증 성공")
		@Test
		void givenEmptyContent_whenRequestComment_thenSuccess () throws Exception {
			//Given
			CommentRequest request1 = new CommentRequest(null);
			CommentRequest request2 = new CommentRequest(null);
			//When
			Set<ConstraintViolation<CommentRequest>> validations1 = validator.validate(request1);
			Set<ConstraintViolation<CommentRequest>> validations2 = validator.validate(request2);
			//Then
			assertThat(validations1).isEmpty();
			assertThat(validations2).isEmpty();
		}

		@DisplayName("댓글 내용이 500자 초과인 경우 유효성 검증 실패")
		@Test
		void givenKorCharUsername_whenRequestSignup_thenThrowException () throws Exception {
			//Given
			CommentRequest request = new CommentRequest("testContent ".repeat(60));
			//When
			Set<ConstraintViolation<CommentRequest>> validations = validator.validate(request);
			//Then
			assertThat(validations).hasSize(1);
		}
	}
}