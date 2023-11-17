package com.sparta.todo.domain;

import com.sparta.todo.domain.constant.MemberRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member")
@Entity
public class Member extends BaseEntity{
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    @Column(name = "username", nullable = false, unique = true)
    private String username;
    @Column(name = "password")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private MemberRole role;

    private Member(String username, String password, MemberRole role) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.createdBy = username;
        this.modifiedBy = username;

    }

    public static Member of(String username, String password) {
        return new Member(username, password, MemberRole.USER);
    }
}
