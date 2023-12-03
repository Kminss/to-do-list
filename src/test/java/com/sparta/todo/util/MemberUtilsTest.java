package com.sparta.todo.util;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.sparta.todo.exception.AccessDeniedException;

class MemberUtilsTest {

	@DisplayName("작성자와 로그인 회원의 아이디가 다른 경우 예외를 발생한다.")
	@Test
	void givenMissMatchWriterIdAndLoginMemberId_whenCheckAuthority_thenThrowException () throws Exception {
	    //Given
	    Long writerMemberId = 1L;
		Long currentMemberId = 2L;
		String message = "ACCESS_DENIED_MEMBER";
	    //When & Then
		assertThatCode(() ->MemberUtils.checkMember(writerMemberId,currentMemberId,message))
			.isInstanceOf(
			AccessDeniedException.class);
	}

	@DisplayName("작성자와 로그인 회원의 아이디가 같은 경우 아무것도 안한다.")
	@Test
	void givenMatchedWriterIdAndLoginMemberId_whenCheckAuthority_thenDoNothing () throws Exception {
		//Given
		Long writerMemberId = 1L;
		Long currentMemberId = 1L;
		String message = "ACCESS_DENIED_MEMBER";

		//When & Then
		assertThatCode(() ->MemberUtils.checkMember(writerMemberId,currentMemberId,message))
			.doesNotThrowAnyException();
	}
}