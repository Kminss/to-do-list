package com.sparta.todo.service;

import com.sparta.todo.domain.Member;
import com.sparta.todo.domain.ToDo;
import com.sparta.todo.dto.MemberDto;
import com.sparta.todo.dto.request.CreateToDoRequest;
import com.sparta.todo.dto.request.SearchToDoCondition;
import com.sparta.todo.dto.request.UpdateToDoRequest;
import com.sparta.todo.dto.response.ToDoResponse;
import com.sparta.todo.exception.MemberNotFoundException;
import com.sparta.todo.exception.ToDoNotFoundException;
import com.sparta.todo.repository.MemberRepository;
import com.sparta.todo.repository.ToDoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.sparta.todo.util.MemberUtils.checkMember;

@Service
@Transactional
public class ToDoService {
    private static final String ACCESS_DENIED_MESSAGE = "해당 ToDo에 대한 권한이 없습니다.";
    private final ToDoRepository toDoRepository;
    private final MemberRepository memberRepository;

    public ToDoService(ToDoRepository toDoRepository, MemberRepository memberRepository) {
        this.toDoRepository = toDoRepository;
        this.memberRepository = memberRepository;
    }

    public ToDoResponse createToDo(CreateToDoRequest toDoRequest, MemberDto memberDto) {
        Member member = memberRepository.findById(memberDto.id())
                .orElseThrow(MemberNotFoundException::new);

        ToDo todo = toDoRepository.save(toDoRequest.toEntity((member)));

        return ToDoResponse.from(todo);
    }

    @Transactional(readOnly = true)
    public List<ToDoResponse> searchToDos(SearchToDoCondition condition) {
        List<ToDo> todos = toDoRepository.searchToDoBy(condition);
        if (todos.isEmpty()) {
            throw new ToDoNotFoundException();
        }

        return toDoRepository.searchToDoBy(condition).stream()
                .map(ToDoResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ToDoResponse getToDo(Long toDoId) {
        ToDo toDo = toDoRepository.findById(toDoId)
                .orElseThrow(ToDoNotFoundException::new);

        return ToDoResponse.from(toDo);
    }

    public ToDoResponse updateToDo(Long toDoId, UpdateToDoRequest request, MemberDto memberDto) {
        ToDo toDo = toDoRepository.findById(toDoId)
                .orElseThrow(ToDoNotFoundException::new);

        checkMember(toDo.getMember().getId(), memberDto.id(), ACCESS_DENIED_MESSAGE);
        toDo.update(request);

        return ToDoResponse.from(toDo);
    }

    public void deleteToDo(Long toDoId, MemberDto memberDto) {
        ToDo toDo = toDoRepository.findById(toDoId)
                .orElseThrow(ToDoNotFoundException::new);

        checkMember(toDo.getMember().getId(), memberDto.id(), ACCESS_DENIED_MESSAGE);
        toDoRepository.delete(toDo);
    }
}
