package com.sparta.todo.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import com.sparta.todo.config.JpaTestConfig;
import com.sparta.todo.config.PasswordEncoderConfig;
import com.sparta.todo.config.QueryDslConfig;
import com.sparta.todo.domain.Member;

@DisplayName("레포지토리 테스트 - Member")
@Import({
	PasswordEncoderConfig.class,
	QueryDslConfig.class,
	JpaTestConfig.class
})
@ActiveProfiles("test")
@DataJpaTest
class MemberRepositoryTest {
	@Autowired
	ToDoRepository toDoRepository;

	@Autowired
	MemberRepository memberRepository;

	@Autowired
	PasswordEncoder passwordEncoder;



	@DisplayName("로그인 아이디로 조회 테스트")
	@Test
	void givenUsername_whenFind_thenReturnMember() throws Exception {
		//Given
		Member member = Member.of("minss1", "minss123");
		Member savedMember = memberRepository.save(member);
		//When
		Optional<Member> findMember = memberRepository.findByUsername(savedMember.getUsername());
		//Then
		assertThat(findMember).isPresent();
	}

	@DisplayName("아이디 중복 조회 테스트")
	@Test
	void givenExistUsername_whenFind_thenReturnBooleanResult() throws Exception {
		//Given
		Member member = Member.of("minss1", "minss123");
		Member savedMember = memberRepository.save(member);
		//When
		boolean result = memberRepository.existsByUsername(savedMember.getUsername());
		//Then
		assertThat(result).isTrue();
	}
}