package com.sparta.todo.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import com.sparta.todo.domain.Member;
import com.sparta.todo.domain.ToDo;
import com.sparta.todo.domain.constant.SearchType;
import com.sparta.todo.dto.MemberDto;
import com.sparta.todo.dto.request.CreateToDoRequest;
import com.sparta.todo.dto.request.SearchToDoCondition;
import com.sparta.todo.dto.request.UpdateToDoRequest;
import com.sparta.todo.dto.response.ToDoResponse;
import com.sparta.todo.exception.AccessDeniedException;
import com.sparta.todo.exception.AlreadyCompleteToDoException;
import com.sparta.todo.exception.AlreadyHiddenToDoException;
import com.sparta.todo.exception.NotCompleteToDoException;
import com.sparta.todo.exception.NotHiddenToDoException;
import com.sparta.todo.exception.ToDoNotFoundException;
import com.sparta.todo.repository.MemberRepository;
import com.sparta.todo.repository.ToDoRepository;

@DisplayName("서비스 테스트 - ToDo")
@ExtendWith(MockitoExtension.class)
class ToDoServiceTest {

	@InjectMocks
	private ToDoService sut;

	@Mock
	private ToDoRepository toDoRepository;

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@DisplayName("할 일 정보를 입력하면 할 일을 생성한다.")
	@Test
	void givenCreateToDoInfoAndMemberInfo_whenSaveToDo_thenSaveToDoAndReturnToDoInfo() throws Exception {
		//Given
		Member member = getMember(1L);

		CreateToDoRequest toDoRequest = new CreateToDoRequest("test title", "test content");
		ToDo toDo = toDoRequest.toEntity(member);
		given(memberRepository.findById(any())).willReturn(Optional.of(member));
		given(toDoRepository.save(any(ToDo.class))).willReturn(toDo);

		//When
		sut.createToDo(toDoRequest, MemberDto.from(member));

		//Then
		then(toDoRepository).should().save(any(ToDo.class));
	}

	@DisplayName("할 일목 3개일 때, 전체 목록 검색 시 목록 반환")
	@Test
	void givenSearchCondition_whenSearchToDo_thenReturnToDoList() {
		// Given
		SearchToDoCondition condition = new SearchToDoCondition(SearchType.TITLE, null);
		Member member = getMember(1L);
		MemberDto memberDto = MemberDto.from(member);
		List<ToDo> todos = List.of(
			ToDo.of("testTitle1", "testContent1", member),
			ToDo.of("testTitle2", "testContent2", member),
			ToDo.of("testTitle3", "testContent3", member)
		);
		given(toDoRepository.searchToDoBy(condition, memberDto)).willReturn(todos);

		//When
		List<ToDoResponse> actual = sut.searchToDos(condition, memberDto);

		//Then
		assertThat(actual).isEqualTo(
			todos.stream()
				.map(ToDoResponse::from)
				.toList()
		);
	}

	@DisplayName("할 일목 3개일 때, 제목  검색 시 맞는 목록 반환") // -> repository 에서 테스트해야하나?
	@Test
	void givenSearchCondition_whenSearchToDoByTitle_thenReturnToDoList() {
		// Given
		SearchToDoCondition condition = new SearchToDoCondition(SearchType.TITLE, "test");
		Member member = getMember(1L);
		MemberDto memberDto = MemberDto.from(member);
		List<ToDo> todos = List.of(
			ToDo.of("title1", "testContent1", member),
			ToDo.of("title2", "testContent2", member),
			ToDo.of("testTitle3", "testContent3", member)
		);

		given(toDoRepository.searchToDoBy(condition, memberDto)).willReturn(
			todos.stream().filter(toDo ->
				toDo.getTitle().contains("test")).toList()
		);

		//When
		List<ToDoResponse> actual = sut.searchToDos(condition, memberDto);

		//Then
		assertThat(actual).hasSize(1);
	}

	@DisplayName("할 일이 0개일 때, 목록 검색 시 예외 발생")
	@Test
	void givenNothing_whenSearchToDo_thenThrowToDoNotFoundException() {
		// Given
		SearchToDoCondition condition = new SearchToDoCondition(SearchType.TITLE, null);
		Member member = getMember(1L);
		MemberDto memberDto = MemberDto.from(member);
		// When & Then
		assertThatThrownBy(() -> sut.searchToDos(condition, memberDto))
			.isInstanceOf(ToDoNotFoundException.class);
	}

	@DisplayName("할 일 ID로 조회하면 해당하는 할 일 정보 반환")
	@Test
	void givenToDoId_whenGetToDo_thenReturnToDo() throws Exception {
		//Given
		Long toDoId = 1L;
		Member member = getMember(1L);
		ToDo toDo = createToDo(toDoId, member);
		given(toDoRepository.findById(toDoId)).willReturn(Optional.of(toDo));

		//When
		ToDoResponse actual = sut.getToDo(toDoId);

		//Then
		assertThat(actual).isEqualTo(ToDoResponse.from(toDo));
	}

	@DisplayName("없는 할 일 ID로 조회하면 예외 발생")
	@Test
	void givenNothingToDoId_whenGetToDo_thenThrowToDoNotFoundException() throws Exception {
		//Given
		Long toDoId = 1L;

		//When & Then
		assertThatThrownBy(() -> sut.getToDo(toDoId))
			.isInstanceOf(ToDoNotFoundException.class);
	}

	@DisplayName("할 일 수정정보를 입력하면, 할일 정보를 수정하고, 할 일을 반환")
	@Test
	void givenUpdateToDoInfo_whenUpdateToDo_thenReturnUpdateToDoInfo() throws Exception {
		//Given
		Member member = getMember(1L);
		MemberDto memberDto = MemberDto.from(member);

		Long toDoId = 1L;
		ToDo toDo = createToDo(toDoId, member);
		UpdateToDoRequest toDoRequest = new UpdateToDoRequest("updateTitle", "updateContent");

		given(toDoRepository.findById(toDoId)).willReturn(Optional.of(toDo));

		//When
		ToDoResponse actual = sut.updateToDo(toDoId, toDoRequest, memberDto);

		//Then
		assertThat(actual)
			.hasFieldOrPropertyWithValue("title", toDoRequest.title())
			.hasFieldOrPropertyWithValue("content", toDoRequest.content());
	}

	@DisplayName("할 일 수정 시 할 일에 대한 수정권한이 없는 경우 예외 발생")
	@Test
	void givenUpdateToDoInfoAndNothingAuthority_whenUpdateToDo_thenThrowException() throws Exception {
		//Given
		Member toDoWriter = getMember(1L);
		MemberDto memberDto = MemberDto.from(getMember(2L));

		Long toDoId = 1L;
		ToDo toDo = createToDo(toDoId, toDoWriter);
		UpdateToDoRequest toDoRequest = new UpdateToDoRequest("updateTitle", "updateContent");

		given(toDoRepository.findById(toDoId)).willReturn(Optional.of(toDo));

		//When & Then
		assertThatThrownBy(() -> sut.updateToDo(toDoId, toDoRequest, memberDto))
			.isInstanceOf(AccessDeniedException.class);
	}

	@DisplayName("삭제할 할일 ID를 입력하면, 할일을 삭제하고 아무것도 반환하지 않는다.")
	@Test
	void givenDeleteToDoId_whenDeleteToDo_thenReturnNothing() throws Exception {
		//Given
		Member member = getMember(1L);
		MemberDto memberDto = MemberDto.from(member);

		Long toDoId = 1L;
		ToDo toDo = createToDo(toDoId, member);
		given(toDoRepository.findById(toDoId)).willReturn(Optional.of(toDo));

		//When
		sut.deleteToDo(toDoId, memberDto);

		//Then
		assertThatCode(() -> sut.deleteToDo(toDoId, memberDto))
			.doesNotThrowAnyException();
	}

	@DisplayName("할 일 삭제 시 할 일에 대한 삭제권한이 없는 경우 예외 발생")
	@Test
	void givenDeleteToDoIdAndNothingAuthority_whenDeleteToDo_thenThrowException() throws Exception {
		//Given
		Member toDoWriter = getMember(1L);
		MemberDto memberDto = MemberDto.from(getMember(2L));

		Long toDoId = 1L;
		ToDo toDo = createToDo(toDoId, toDoWriter);

		given(toDoRepository.findById(toDoId)).willReturn(Optional.of(toDo));

		//When & Then
		assertThatThrownBy(() -> sut.deleteToDo(toDoId, memberDto))
			.isInstanceOf(AccessDeniedException.class);
	}

	@DisplayName("할 일 완료 요청 시 할 일 완료 처리")
	@Test
	void givenToDoId_whenCompleteToDo_thenUpdateComplete() throws Exception {
		//Given
		Member toDoWriter = getMember(1L);
		MemberDto memberDto = MemberDto.from(toDoWriter);

		Long toDoId = 1L;
		ToDo toDo = createToDo(toDoId, toDoWriter);

		given(toDoRepository.findById(toDoId)).willReturn(Optional.of(toDo));

		//When
		sut.completeToDo(toDoId, memberDto);

		//Then
		assertThat(toDo.isDone()).isTrue();
	}

	@DisplayName("할 일 완료 시 이미 완료처리된 할 일인 경우 예외 발생")
	@Test
	void givenAlreadyExistCompleteToDoId_whenCompleteToDo_thenThrowException() throws Exception {
		//Given
		Member toDoWriter = getMember(1L);
		MemberDto memberDto = MemberDto.from(toDoWriter);

		Long toDoId = 1L;
		ToDo toDo = createToDo(toDoId, toDoWriter);
		toDo.complete(true);

		given(toDoRepository.findById(toDoId)).willReturn(Optional.of(toDo));

		//When & Then
		assertThatThrownBy(() -> sut.completeToDo(toDoId, memberDto))
			.isInstanceOf(AlreadyCompleteToDoException.class);
	}

	@DisplayName("할 일 완료 취소 요청 시 할 일 완료 취소 처리")
	@Test
	void givenToDoId_whenCancelCompleteToDo_thenCancelComplete() throws Exception {
		//Given
		Member toDoWriter = getMember(1L);
		MemberDto memberDto = MemberDto.from(toDoWriter);

		Long toDoId = 1L;
		ToDo toDo = createToDo(toDoId, toDoWriter);
		toDo.complete(true);
		given(toDoRepository.findById(toDoId)).willReturn(Optional.of(toDo));

		//When
		sut.deleteCompleteToDo(toDoId, memberDto);

		//Then
		assertThat(toDo.isDone()).isFalse();
	}

	@DisplayName("할 일 완료 취소 시 이미 미완료 할 일인 경우 예외 발생")
	@Test
	void givenNotCompleteToDoId_whenCancelCompleteToDo_thenThrowException() throws Exception {
		//Given
		Member toDoWriter = getMember(1L);
		MemberDto memberDto = MemberDto.from(toDoWriter);

		Long toDoId = 1L;
		ToDo toDo = createToDo(toDoId, toDoWriter);
		toDo.complete(false);

		given(toDoRepository.findById(toDoId)).willReturn(Optional.of(toDo));

		//When & Then
		assertThatThrownBy(() -> sut.deleteCompleteToDo(toDoId, memberDto))
			.isInstanceOf(NotCompleteToDoException.class);
	}

	@DisplayName("할 일 비공개 요청 시 할 일 비공개 처리")
	@Test
	void givenToDoId_whenHiddenToDo_thenUpdateHidden() throws Exception {
		//Given
		Member toDoWriter = getMember(1L);
		MemberDto memberDto = MemberDto.from(toDoWriter);

		Long toDoId = 1L;
		ToDo toDo = createToDo(toDoId, toDoWriter);

		given(toDoRepository.findById(toDoId)).willReturn(Optional.of(toDo));

		//When
		sut.hiddenToDo(toDoId, memberDto);

		//Then
		assertThat(toDo.isHidden()).isTrue();
	}

	@DisplayName("할 일 비공개 요청 시 이미 비공개된 할 일인 경우 예외 발생")
	@Test
	void givenAlreadyHiddenToDoId_whenHiddenToDo_thenThrowException() throws Exception {
		//Given
		Member toDoWriter = getMember(1L);
		MemberDto memberDto = MemberDto.from(toDoWriter);

		Long toDoId = 1L;
		ToDo toDo = createToDo(toDoId, toDoWriter);
		toDo.hidden(true);

		given(toDoRepository.findById(toDoId)).willReturn(Optional.of(toDo));

		//When & Then
		assertThatThrownBy(() -> sut.hiddenToDo(toDoId, memberDto))
			.isInstanceOf(AlreadyHiddenToDoException.class);
	}

	@DisplayName("할 일 비공개 취소 요청 시 할 일 비공개 취소 처리")
	@Test
	void givenHiddenToDoId_whenCancelHiddenToDo_thenDeleteHidden() throws Exception {
		//Given
		Member toDoWriter = getMember(1L);
		MemberDto memberDto = MemberDto.from(toDoWriter);

		Long toDoId = 1L;
		ToDo toDo = createToDo(toDoId, toDoWriter);
		toDo.hidden(true);
		given(toDoRepository.findById(toDoId)).willReturn(Optional.of(toDo));

		//When
		sut.cancelHiddenToDo(toDoId, memberDto);

		//Then
		assertThat(toDo.isHidden()).isFalse();
	}

	@DisplayName("할 일 비공개 취소 요청시 공개 할 일인 경우 예외 발생")
	@Test
	void givenNotHiddenToDoId_whenCancelHiddenToDo_thenThrowException() throws Exception {
		//Given
		Member toDoWriter = getMember(1L);
		MemberDto memberDto = MemberDto.from(toDoWriter);

		Long toDoId = 1L;
		ToDo toDo = createToDo(toDoId, toDoWriter);
		toDo.hidden(false);

		given(toDoRepository.findById(toDoId)).willReturn(Optional.of(toDo));

		//When & Then
		assertThatThrownBy(() -> sut.cancelHiddenToDo(toDoId, memberDto))
			.isInstanceOf(NotHiddenToDoException.class);
	}

	private Member getMember(Long memberId) {
		String username = "minss1";
		String password = "minss1";
		Member member = Member.of(username, passwordEncoder.encode(password));

		ReflectionTestUtils.setField(member, "id", memberId);
		return member;
	}

	private ToDo createToDo(Long toDoId, Member member) {
		ToDo toDo = ToDo.of(
			"testName",
			"testTitle",
			member
		);

		ReflectionTestUtils.setField(toDo, "id", toDoId);
		ReflectionTestUtils.setField(member, "id", 1L);
		return toDo;
	}

}