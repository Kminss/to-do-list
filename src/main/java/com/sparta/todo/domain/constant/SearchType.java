package com.sparta.todo.domain.constant;

import java.util.Arrays;

public enum SearchType {
    TITLE;


    public static SearchType findMatchedEnum(String searchType) {
        return Arrays.stream(SearchType.values())
                .filter(type ->
                        type.name().equals(searchType.toUpperCase())
                )
                .findFirst()
                .orElse(SearchType.TITLE);
    }
}
