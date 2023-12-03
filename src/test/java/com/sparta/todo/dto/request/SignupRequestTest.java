package com.sparta.todo.dto.request;



import static org.assertj.core.api.Assertions.*;

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

@DisplayName("회원가입 요청 유효성 검증")
class SignupRequestTest {

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

	@DisplayName("아이디 유효성 검증")
	@Nested
	class UsernameTest{

		@DisplayName("아이디가 4자리일 때 유효성 검증 성공")
		@Test
		void given4LengthUsername_whenRequestSignup_thenSuccess () throws Exception {
			//Given
			SignupRequest request = new SignupRequest("test", "testpassword");
			//When
			Set<ConstraintViolation<SignupRequest>> validations = validator.validate(request);
			//Then
			assertThat(validations).isEmpty();
		}

		@DisplayName("아이디가 8자리일 때 유효성 검증 성공")
		@Test
		void given8LengthUsername_whenRequestSignup_thenSuccess () throws Exception {
			//Given
			SignupRequest request = new SignupRequest("testtest", "testpassword");
			//When
			Set<ConstraintViolation<SignupRequest>> validations = validator.validate(request);
			//Then
			assertThat(validations).isEmpty();
		}

		@DisplayName("아이디가 소문자, 숫자 입력할 때 유효성 검증 성공")
		@Test
		void givenAlphaAndNumsUsername_whenRequestSignup_thenSuccess () throws Exception {
			//Given
			SignupRequest request = new SignupRequest("test1234", "testpassword");
			//When
			Set<ConstraintViolation<SignupRequest>> validations = validator.validate(request);
			//Then
			assertThat(validations).isEmpty();
		}


		@DisplayName("아이디를 빈값으로 입력한 경우 유효성 검증 실패")
		@Test
		void givenEmptyUsername_whenRequestSignup_thenThrowException () throws Exception {
			//Given
			SignupRequest request = new SignupRequest(null, "testpassword");
			//When
			Set<ConstraintViolation<SignupRequest>> validations = validator.validate(request);
			//Then
			assertThat(validations).hasSize(1);
		}


		@DisplayName("아이디에 대문자 영어가 존재하는 경우 유효성 검증 실패")
		@Test
		void givenUpperCaseUsername_whenRequestSignup_thenThrowException () throws Exception {
			//Given
			SignupRequest request = new SignupRequest("Testuser", "testpassword");
			//When
			Set<ConstraintViolation<SignupRequest>> validations = validator.validate(request);
			//Then
			assertThat(validations).hasSize(1);
		}

		@DisplayName("아이디에 특수문자가 존재하는 경우 유효성 검증 실패")
		@Test
		void givenSpecialCharUsername_whenRequestSignup_thenThrowException () throws Exception {
			//Given
			SignupRequest request = new SignupRequest("test@user", "testpassword");
			//When
			Set<ConstraintViolation<SignupRequest>> validations = validator.validate(request);
			//Then
			assertThat(validations).hasSize(1);
		}

		@DisplayName("아이디에 4자리 미만인 경우 유효성 검증 실패")
		@Test
		void givenUnder4LengthUsername_whenRequestSignup_thenThrowException () throws Exception {
			//Given
			SignupRequest request = new SignupRequest("tes", "testpassword");
			//When
			Set<ConstraintViolation<SignupRequest>> validations = validator.validate(request);
			//Then
			assertThat(validations).hasSize(1);
		}
		@DisplayName("아이디에 10자리 초과인 경우 유효성 검증 실패")
		@Test
		void givenOver10LengthUsername_whenRequestSignup_thenThrowException () throws Exception {
			//Given
			SignupRequest request = new SignupRequest("testtesttes", "testpassword");
			//When
			Set<ConstraintViolation<SignupRequest>> validations = validator.validate(request);
			//Then
			assertThat(validations).hasSize(1);
		}

		@DisplayName("아이디에 한글 입력된 경우 유효성 검증 실패")
		@Test
		void givenKorCharUsername_whenRequestSignup_thenThrowException () throws Exception {
			//Given
			SignupRequest request = new SignupRequest("테스트유저", "testpassword");
			//When
			Set<ConstraintViolation<SignupRequest>> validations = validator.validate(request);
			//Then
			assertThat(validations).hasSize(1);
		}
	}

	@DisplayName("비밀번호 유효성 검증")
	@Nested
	class PasswordTest{

		@DisplayName("비밀번호가 8자리일 때 유효성 검증 성공")
		@Test
		void given8LengthPassword_whenRequestSignup_thenSuccess () throws Exception {
			//Given
			SignupRequest request = new SignupRequest("testuser", "testpass");
			//When
			Set<ConstraintViolation<SignupRequest>> validations = validator.validate(request);
			//Then
			assertThat(validations).isEmpty();
		}

		@DisplayName("비밀번호가 15자리일 때 유효성 검증 성공")
		@Test
		void given15LengthPassword_whenRequestSignup_thenSuccess () throws Exception {
			//Given
			SignupRequest request = new SignupRequest("testuser", "testpassword123");
			//When
			Set<ConstraintViolation<SignupRequest>> validations = validator.validate(request);
			//Then
			assertThat(validations).isEmpty();
		}

		@DisplayName("비밀번호가 영어 대/소문자, 숫자 입력할 때 유효성 검증 성공")
		@Test
		void givenAlphaAndNumsPassword_whenRequestSignup_thenSuccess () throws Exception {
			//Given
			SignupRequest request = new SignupRequest("testuser", "TestPass123456");
			//When
			Set<ConstraintViolation<SignupRequest>> validations = validator.validate(request);
			//Then
			assertThat(validations).isEmpty();
		}


		@DisplayName("비밀번호를 빈값으로 입력한 경우 유효성 검증 실패")
		@Test
		void givenEmptyPassword_whenRequestSignup_thenThrowException () throws Exception {
			//Given
			SignupRequest request = new SignupRequest("testuser", null);
			//When
			Set<ConstraintViolation<SignupRequest>> validations = validator.validate(request);
			//Then
			assertThat(validations).hasSize(1);
		}

		@DisplayName("비밀번호에 특수문자가 존재하는 경우 유효성 검증 실패")
		@Test
		void givenSpecialCharPassword_whenRequestSignup_thenThrowException () throws Exception {
			//Given
			SignupRequest request = new SignupRequest("testuser", "testp@ssw!rd");
			//When
			Set<ConstraintViolation<SignupRequest>> validations = validator.validate(request);
			//Then
			assertThat(validations).hasSize(1);
		}

		@DisplayName("비밀번호가 8자리 미만인 경우 유효성 검증 실패")
		@Test
		void givenUnder8LengthPassword_whenRequestSignup_thenThrowException () throws Exception {
			//Given
			SignupRequest request = new SignupRequest("testuser", "testpas");
			//When
			Set<ConstraintViolation<SignupRequest>> validations = validator.validate(request);
			//Then
			assertThat(validations).hasSize(1);
		}
		@DisplayName("비밀번호가 15자리 초과인 경우 유효성 검증 실패")
		@Test
		void givenOver15LengthPassword_whenRequestSignup_thenThrowException () throws Exception {
			//Given
			SignupRequest request = new SignupRequest("testuser", "testPassword12345");
			//When
			Set<ConstraintViolation<SignupRequest>> validations = validator.validate(request);
			//Then
			assertThat(validations).hasSize(1);
		}

		@DisplayName("비밀번호에 한글 입력된 경우 유효성 검증 실패")
		@Test
		void givenKorCharPassword_whenRequestSignup_thenThrowException () throws Exception {
			//Given
			SignupRequest request = new SignupRequest("testuser", "테스트비밀번호입니다");
			//When
			Set<ConstraintViolation<SignupRequest>> validations = validator.validate(request);
			//Then
			assertThat(validations).hasSize(1);
		}
	}

}