package com.sparta.todo.repository.querydsl;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.BooleanPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.todo.domain.ToDo;
import com.sparta.todo.domain.constant.SearchType;
import com.sparta.todo.dto.MemberDto;
import com.sparta.todo.dto.request.SearchToDoCondition;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.sparta.todo.domain.QToDo.toDo;

@Repository
public class ToDoRepositoryCustomImpl implements ToDoRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    public ToDoRepositoryCustomImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }


    @Override
    public List<ToDo> searchToDoBy(SearchToDoCondition condition, MemberDto memberDto) {
        return jpaQueryFactory.selectFrom(toDo)
                .where(
                        searchKeyword(condition.searchType(), condition.searchKeyword()),
                        hiddenFilter(toDo.isHidden, memberDto)

                )
                .orderBy(new OrderSpecifier<>(Order.DESC, toDo.createdDateTime))
                .fetch();

    }

    private Predicate searchKeyword(SearchType searchType, String searchKeyword) {
        if (!StringUtils.hasText(searchKeyword) || searchType == null) {
            return null;
        }

        return switch (searchType) {
            case TITLE -> toDo.title.contains(searchKeyword);
        };
    }

    private BooleanExpression hiddenFilter(BooleanPath isHidden, MemberDto memberDto) {
        return isHidden.isFalse().or(toDo.member.id.eq(memberDto.id()));
    }
}
