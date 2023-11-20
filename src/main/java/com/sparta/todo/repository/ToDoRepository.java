package com.sparta.todo.repository;

import com.sparta.todo.domain.ToDo;
import com.sparta.todo.repository.querydsl.ToDoRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ToDoRepository extends JpaRepository<ToDo, Long>, ToDoRepositoryCustom {
}
