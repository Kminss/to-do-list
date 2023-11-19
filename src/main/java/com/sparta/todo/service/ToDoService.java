package com.sparta.todo.service;

import com.sparta.todo.domain.Member;
import com.sparta.todo.domain.ToDo;
import com.sparta.todo.dto.MemberDto;
import com.sparta.todo.dto.request.CreateToDoRequest;
import com.sparta.todo.dto.response.CreateToDoResponse;
import com.sparta.todo.exception.MemberNotFoundException;
import com.sparta.todo.repository.MemberRepository;
import com.sparta.todo.repository.ToDoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ToDoService {
    private final ToDoRepository toDoRepository;
    private final MemberRepository memberRepository;

    public ToDoService(ToDoRepository toDoRepository, MemberRepository memberRepository) {
        this.toDoRepository = toDoRepository;
        this.memberRepository = memberRepository;
    }

    public CreateToDoResponse createToDo(CreateToDoRequest toDoRequest, MemberDto memberDto) {
        Member member = memberRepository.findById(memberDto.id())
                .orElseThrow(MemberNotFoundException::new);

        ToDo todo = toDoRepository.save(toDoRequest.toEntity((member)));

        return CreateToDoResponse.from(todo);
    }


}
