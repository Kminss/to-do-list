package com.sparta.todo.domain.constant;

public enum MemberRole {
    USER("ROLE_USER"),  // 사용자 권한

    ADMIN("ROLE_ADMIN"); // 관리자 권한

    private final String authority;

    MemberRole(String authority) {
        this.authority = authority;
    }

    public String getAuthority() {
        return authority;
    }
}
