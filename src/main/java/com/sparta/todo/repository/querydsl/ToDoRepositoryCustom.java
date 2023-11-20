package com.sparta.todo.repository.querydsl;

import com.sparta.todo.domain.ToDo;
import com.sparta.todo.dto.request.SearchToDoCondition;

import java.util.List;

public interface ToDoRepositoryCustom {
    List<ToDo> searchToDoBy(SearchToDoCondition condition);
}
