package com.sparta.todo.domain;

import com.sparta.todo.dto.request.UpdateToDoRequest;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "todo")
@Entity
public class ToDo extends BaseEntity{
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    @Column(name = "title", nullable = false, length = 300)
    private String title;

    @Column(name = "content", length = 1000)
    private String content;

    @ColumnDefault("false")
    @Column(name = "is_done", nullable = false)
    private Boolean isDone = false;

    @JoinColumn(name = "member_id")
    @ManyToOne(optional = false)  //optional: 외래키 not null 제약조건
    private Member member;

    @OrderBy("createdDateTime DESC")
    @OneToMany(
            fetch = FetchType.LAZY,
            cascade = CascadeType.REMOVE,
            orphanRemoval = true,
            mappedBy = "todo")
    private Set<Comment> comments = new LinkedHashSet<>();


    public ToDo(String title, String content, Member member) {
        this.title = title;
        this.content = content;
        this.member = member;
    }

    public static ToDo of(String title, String content, Member member) {
        return new ToDo(title, content, member);
    }



}
