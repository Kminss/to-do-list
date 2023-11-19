package com.sparta.todo.annotaion;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// 런타임 까지 유지
@Retention(RetentionPolicy.RUNTIME)
// 타겟은 파라미터에만 붙이겠다.
@Target(ElementType.PARAMETER)
// 익명 사용자인 경우에는 null로, 익명 사용자가 아닌 경우에는 실제 CustomuUserDetails의 memberDto 객체로
// Principal 을 다이나믹 하게 꺼내기 위해 @CurrentMember 생성
@AuthenticationPrincipal(expression = "#this == 'anonymousUser' ? null : memberDto")
public @interface CurrentMember {
}
