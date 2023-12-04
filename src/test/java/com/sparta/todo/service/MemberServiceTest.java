package com.sparta.todo.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.sparta.todo.config.PasswordEncoderConfig;
import com.sparta.todo.domain.Member;
import com.sparta.todo.dto.request.SignupRequest;
import com.sparta.todo.exception.AlreadyExistMemberException;
import com.sparta.todo.repository.MemberRepository;

@DisplayName("서비스 테스트 - Member")
@Import(PasswordEncoderConfig.class)
@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

	@InjectMocks
	private MemberService sut;

	@Mock
	private MemberRepository memberRepository;
	@Mock
	private PasswordEncoder passwordEncoder;

	@DisplayName("회원가입 성공 테스트")
	@Test
	void givenSignupRequest_whenSignup_thenSignup () throws Exception {

	    //Given
		SignupRequest request = new SignupRequest("testuser", "testuser1234");
		given(memberRepository.existsByUsername(request.username())).willReturn(false);
	    //When
		sut.signup(request);
	    //Then
		then(memberRepository).should().save(any(Member.class));
	}

	@DisplayName("회원가입 실패 - 아이디 중복 테스트")
	@Test
	void givenSignupRequest_whenSignup_thenThrowException () throws Exception {
		//Given
		SignupRequest request = new SignupRequest("testuser", "testuser1234");
		given(memberRepository.existsByUsername(request.username())).willReturn(true);

		//When && Then
		assertThatCode(() -> sut.signup(request))
			.isInstanceOf(AlreadyExistMemberException.class);
	}
}