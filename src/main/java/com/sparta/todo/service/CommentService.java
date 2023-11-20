package com.sparta.todo.service;

import com.sparta.todo.domain.Comment;
import com.sparta.todo.domain.Member;
import com.sparta.todo.domain.ToDo;
import com.sparta.todo.dto.MemberDto;
import com.sparta.todo.dto.request.CommentRequest;
import com.sparta.todo.dto.response.CommentResponse;
import com.sparta.todo.exception.CommentNotFoundException;
import com.sparta.todo.exception.MemberNotFoundException;
import com.sparta.todo.exception.ToDoNotFoundException;
import com.sparta.todo.repository.CommentRepository;
import com.sparta.todo.repository.MemberRepository;
import com.sparta.todo.repository.ToDoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.sparta.todo.util.MemberUtils.checkMember;

@RequiredArgsConstructor
@Transactional
@Service
public class CommentService {
    private static final String ACCESS_DENIED_MESSAGE = "해당 댓글에 대한 권한이 없습니다.";
    private final CommentRepository commentRepository;
    private final ToDoRepository toDoRepository;
    private final MemberRepository memberRepository;


    public CommentResponse createComment(Long toDoId, CommentRequest request, MemberDto memberDto) {
        Member member = memberRepository.findById(memberDto.id())
                .orElseThrow(MemberNotFoundException::new);

        ToDo toDo = toDoRepository.findById(toDoId)
                .orElseThrow(ToDoNotFoundException::new);

        Comment savedComment = commentRepository.save(request.toEntity(member, toDo));

        return CommentResponse.from(savedComment);
    }

    public CommentResponse updateComment(Long commentId, CommentRequest request, MemberDto memberDto) {
        Comment comment = commentRepository.findById(commentId)
                        .orElseThrow(CommentNotFoundException::new);

        checkMember(comment.getMember().getId(), memberDto.id(), ACCESS_DENIED_MESSAGE);
        comment.update(request);

        return CommentResponse.from(comment);
    }
}
