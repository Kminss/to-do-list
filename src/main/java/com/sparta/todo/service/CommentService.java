package com.sparta.todo.service;

import com.sparta.todo.domain.Comment;
import com.sparta.todo.domain.Member;
import com.sparta.todo.domain.ToDo;
import com.sparta.todo.dto.MemberDto;
import com.sparta.todo.dto.request.CreateCommentRequest;
import com.sparta.todo.dto.response.CommentResponse;
import com.sparta.todo.exception.MemberNotFoundException;
import com.sparta.todo.exception.ToDoNotFoundException;
import com.sparta.todo.repository.CommentRepository;
import com.sparta.todo.repository.MemberRepository;
import com.sparta.todo.repository.ToDoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@RequiredArgsConstructor
@Transactional
@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final ToDoRepository toDoRepository;
    private final MemberRepository memberRepository;


    public CommentResponse createComment(Long toDoId, CreateCommentRequest request, MemberDto memberDto) {
        Member member = memberRepository.findById(memberDto.id())
                .orElseThrow(MemberNotFoundException::new);

        ToDo toDo = toDoRepository.findById(toDoId)
                .orElseThrow(ToDoNotFoundException::new);

        Comment savedComment = commentRepository.save(request.toEntity(member, toDo));

        return CommentResponse.from(savedComment);
    }
}
