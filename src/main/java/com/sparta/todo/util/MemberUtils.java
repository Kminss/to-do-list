package com.sparta.todo.util;

import com.sparta.todo.exception.AccessDeniedException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberUtils {

    public static void checkMember(Long writeMemberId, Long currentMemberId, String message) {
        if (!writeMemberId.equals(currentMemberId)) {
            throw new AccessDeniedException(message);
        }
    }
}
