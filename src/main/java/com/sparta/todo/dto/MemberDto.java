package com.sparta.todo.dto;

import com.sparta.todo.domain.Member;
import com.sparta.todo.domain.constant.MemberRole;
import lombok.Builder;
import org.springframework.security.crypto.password.PasswordEncoder;

public record MemberDto(
        Long id,
        String username,
        String password,
        MemberRole role
) {
    @Builder
    public MemberDto {
    }

    public static MemberDto from(Member entity) {
        return new MemberDto(
                entity.getId(),
                entity.getUsername(),
                entity.getPassword(),
                entity.getRole()
        );
    }


    public Member toEntity(PasswordEncoder passwordEncoder) {
        return Member.of(
                username,
                passwordEncoder.encode(password)
        );
    }
}
