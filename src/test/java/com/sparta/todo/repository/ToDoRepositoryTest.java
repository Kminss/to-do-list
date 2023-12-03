package com.sparta.todo.repository;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
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
import com.sparta.todo.domain.ToDo;
import com.sparta.todo.domain.constant.SearchType;
import com.sparta.todo.dto.MemberDto;
import com.sparta.todo.dto.request.SearchToDoCondition;
import com.sparta.todo.dto.request.UpdateToDoRequest;

@DisplayName("레포지토리 테스트 - ToDo")
@Import({
	PasswordEncoderConfig.class,
	QueryDslConfig.class,
	JpaTestConfig.class
})
@ActiveProfiles("test")
@DataJpaTest
class ToDoRepositoryTest {

	@Autowired
	ToDoRepository toDoRepository;

	@Autowired
	MemberRepository memberRepository;

	@Autowired
	PasswordEncoder passwordEncoder;
	Member member;

	@BeforeEach
	void init() {
		Member member = Member.of("minss1", "minss123");
		this.member = memberRepository.save(member);
	}

	@DisplayName("할 일 저장 테스트")
	@Test
	void givenCreateToDoInfo_whenSave_thenReturnSavedToDo() throws Exception {
		//Given
		ToDo toDo = createToDo(member);
		//When
		ToDo savedToDo = toDoRepository.save(toDo);
		//Then
		assertThat(savedToDo).isNotNull();
		assertThat(savedToDo.getId()).isEqualTo(toDo.getId());
	}

	@DisplayName("할 일 조회 테스트")
	@Test
	void givenToDoId_whenFind_thenReturnToDo() throws Exception {
		//Given
		ToDo toDo = createToDo(member);
		ToDo savedToDo = toDoRepository.save(toDo);

		//When.
		Optional<ToDo> findToDo = toDoRepository.findById(savedToDo.getId());

		//Then
		assertThat(findToDo).isPresent();
		assertThat(findToDo.get().getId()).isEqualTo(savedToDo.getId());
	}

	@DisplayName("할 일 수정 테스트")
	@Test
	void givenUpdateToDoInfo_whenUpdate_thenReturnUpdateToDo() throws Exception {
		//Given
		String updateTitle = "test update title";
		String updateContent = "test update content";
		UpdateToDoRequest request = new UpdateToDoRequest(updateTitle, updateContent);

		ToDo toDo = createToDo(member);
		ToDo savedToDo = toDoRepository.save(toDo);

		//When
		savedToDo.update(request);

		//Then
		Optional<ToDo> findToDo = toDoRepository.findById(savedToDo.getId());
		assertThat(findToDo).isPresent();
		assertThat(findToDo.get().getId()).isEqualTo(savedToDo.getId());
		assertThat(findToDo.get().getTitle()).isEqualTo(updateTitle);
		assertThat(findToDo.get().getContent()).isEqualTo(updateContent);
	}

	@DisplayName("할 일 삭제 테스트")
	@Test
	void givenDeleteToDoId_whenDelete_thenNothing() throws Exception {
		//Given
		String updateTitle = "test update title";
		String updateContent = "test update content";
		UpdateToDoRequest request = new UpdateToDoRequest(updateTitle, updateContent);

		ToDo toDo = createToDo(member);
		ToDo savedToDo = toDoRepository.save(toDo);

		//When
		toDoRepository.delete(savedToDo);

		//Then
		Optional<ToDo> findToDo = toDoRepository.findById(savedToDo.getId());
		assertThat(findToDo).isEmpty();
	}

	@DisplayName("제목 검색 테스트")
	@Test
	void givenSearchToDoCondition_whenSearchToDo_thenResultToDoInfos() throws Exception {
		//Given
		SearchToDoCondition condition = SearchToDoCondition.of(SearchType.TITLE, "test");
		List<ToDo> savedList = toDoRepository.saveAll(
			List.of(
				ToDo.of("test title1", "test content1", member),
				ToDo.of("test title2", "test content2", member),
				ToDo.of("title3", "test content3", member)
			)
		);

		//When
		List<ToDo> searchToDos = toDoRepository.searchToDoBy(condition, MemberDto.from(member));

		//Then
		assertAll(
			() -> assertThat(searchToDos).hasSize(2),
			() -> assertThat(searchToDos).anyMatch((toDo) -> toDo.equals(savedList.get(0))),
			() -> assertThat(searchToDos).anyMatch((toDo) -> toDo.equals(savedList.get(1)))
		);
	}

	@DisplayName("정렬 테스트")
	@Test
	void givenSearchToDoCondition_whenSearchToDo_thenResultToDoInfosOrderByCreatedTime() throws Exception {
		//Given
		SearchToDoCondition condition = SearchToDoCondition.of(SearchType.TITLE, "");
		List<ToDo> savedList = toDoRepository.saveAll(
			List.of(
				ToDo.of("test title1", "test content1", member),
				ToDo.of("test title2", "test content2", member),
				ToDo.of("test title3", "test content3", member)
			)
		);

		//When
		List<ToDo> searchToDos = toDoRepository.searchToDoBy(condition, MemberDto.from(member));

		//Then
		assertThat(searchToDos).isNotNull();

		assertAll(
			() -> assertThat(searchToDos).hasSize(3),
			() -> assertThat(searchToDos.get(0)).isEqualTo(savedList.get(2)),
			() -> assertThat(searchToDos.get(1)).isEqualTo(savedList.get(1)),
			() -> assertThat(searchToDos.get(2)).isEqualTo(savedList.get(0))
		);
	}

	@DisplayName("다른회원이 조회할 때 비공개 필터 테스트")
	@Test
	void givenSearchToDoConditionAndNotWriterMember_whenSearchToDo_thenResultToDoInfosOrderByNotHidden() throws
		Exception {
		//Given
		SearchToDoCondition condition = SearchToDoCondition.of(SearchType.TITLE, "");
		List<ToDo> toDos = List.of(
			ToDo.of("test title1", "test content1", member),
			ToDo.of("test title2", "test content2", member),
			ToDo.of("test title3", "test content3", member)
		);
		toDos.get(0).hidden(true);
		toDos.get(1).hidden(true);

		List<ToDo> savedList = toDoRepository.saveAll(toDos);
		Member anothoreMember = Member.of("testuser2", "testuser222");
		memberRepository.save(anothoreMember);

		//When
		List<ToDo> searchToDos = toDoRepository.searchToDoBy(condition, MemberDto.from(anothoreMember));

		//Then
		assertThat(searchToDos).isNotNull();

		assertAll(
			() -> assertThat(searchToDos).hasSize(1),
			() -> assertThat(searchToDos.get(0)).isEqualTo(savedList.get(2))
		);
	}

	private ToDo createToDo(Member member) {
		return ToDo.of(
			"testName",
			"testTitle",
			member
		);
	}

}