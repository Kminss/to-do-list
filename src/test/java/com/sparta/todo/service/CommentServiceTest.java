package com.sparta.todo.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import com.sparta.todo.domain.Comment;
import com.sparta.todo.domain.Member;
import com.sparta.todo.domain.ToDo;
import com.sparta.todo.dto.MemberDto;
import com.sparta.todo.dto.request.CommentRequest;
import com.sparta.todo.dto.response.CommentResponse;
import com.sparta.todo.repository.CommentRepository;
import com.sparta.todo.repository.MemberRepository;
import com.sparta.todo.repository.ToDoRepository;

@DisplayName("서비스 테스트 - ToDo")
@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
	@InjectMocks
	private CommentService sut;

	@Mock
	private CommentRepository commentRepository;

	@Mock
	private ToDoRepository toDoRepository;

	@Mock
	private MemberRepository memberRepository;
	@Mock
	private PasswordEncoder passwordEncoder;

	@DisplayName("댓글 생성 테스트")
	@Test
	void givenToDoIdAndRequestAndMemberInfo_whenCreate_thenCreateAndReturnComment () throws Exception {
	    //Given
		CommentRequest request = new CommentRequest("testContent");
		Member member = getMember(1L);
		ToDo toDo = createToDo(1L, member);
		Comment comment = new Comment(member, toDo, request.content());

		given(memberRepository.findById(any())).willReturn(Optional.of(member));
		given(toDoRepository.findById(any())).willReturn(Optional.of(toDo));
		given(commentRepository.save(any())).willReturn(comment);

	    //When
		CommentResponse savedComment = sut.createComment(any(), request, MemberDto.from(member));

	    //Then
		then(commentRepository).should().save(any(Comment.class));
		assertThat(savedComment).isNotNull();
	}

	@DisplayName("댓글 수정 테스트")
	@Test
	void givenCommentIdAndRequestAndMemberInfo_whenUpdate_thenUpdateAndReturnComment () throws Exception {
		//Given
		CommentRequest request = new CommentRequest("updateTestComment");
		Member member = getMember(1L);
		ToDo toDo = createToDo(1L, member);
		Comment comment = new Comment(member, toDo, "testComment");

		given(commentRepository.findById(any())).willReturn(Optional.of(comment));

		//When
		CommentResponse updateComment = sut.updateComment(1L, request, MemberDto.from(member));

		//Then
		assertThat(updateComment.content()).isEqualTo(request.content());
	}

	@DisplayName("댓글 삭제 테스트")
	@Test
	void givenCommentIdAndMemberInfo_whenDelete_thenSuccess() throws Exception {
		//Given
		Member member = getMember(1L);
		ToDo toDo = createToDo(1L, member);
		Comment comment = new Comment(member, toDo, "testComment");

		given(commentRepository.findById(any())).willReturn(Optional.of(comment));

		//When
		sut.deleteComment(1L, MemberDto.from(member));

		//Then
		then(commentRepository).should().delete(comment);
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