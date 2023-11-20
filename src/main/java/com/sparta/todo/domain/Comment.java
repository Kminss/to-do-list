package com.sparta.todo.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "comment")
@Entity
public class Comment extends BaseEntity{
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @JoinColumn(name = "member_id")
    @ManyToOne(optional = false)  //optional: 외래키 not null 제약조건
    private Member member;

    @ManyToOne
    private ToDo todo;

    @Column(name = "content", length = 500)
    private String content;

    public Comment(Member member, ToDo toDo, String content) {
        this.member = member;
        this.todo = toDo;
        this.content = content;
    }


    public static Comment of(Member member, ToDo toDo, String content) {
        return new Comment(member, toDo, content);
    }
}
