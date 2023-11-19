package com.sparta.todo.dto.request;

import com.sparta.todo.domain.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.security.crypto.password.PasswordEncoder;
@Schema(description = "회원가입 요청")
public record SignupRequest(
        @Schema(description = "로그인 아이디", nullable = false, example = "username12")
        @Pattern(
                regexp = "^[a-z0-9]+$",
                message = "아이디는 알파벳 소문자, 숫자의 조합으로 입력해야합니다."
        )
        @Size(
                min = 4, max = 10,
                message = "아이디는 4자리 이상 10자리 이하로 입력해야합니다."
        )
        String username,

        @Schema(description = "로그인 비밀번호", nullable = false, example = "password12")
        @Pattern(
                regexp = "^[a-zA-Z0-9]+$",
                message = "비밀번호는 알파벳 대/소문자, 숫자의 조합으로 입력해야합니다."
        )
        @Size(
                min = 8,
                max = 15,
                message = "비밀번호는 8자리 이상, 15자리 이하로 입력해야합니다."
        )
        String password
) {

        public Member toEntity(PasswordEncoder passwordEncoder) {
                return Member.of(username, passwordEncoder.encode(password));
        }
}
