package com.sparta.todo.dto.request;

import com.sparta.todo.domain.constant.SearchType;

public record SearchToDoCondition(
        SearchType searchType,
        String searchKeyword
) {
    public static SearchToDoCondition of(SearchType searchType, String searchKeyword) {
        return new SearchToDoCondition(searchType, searchKeyword);
    }
}
