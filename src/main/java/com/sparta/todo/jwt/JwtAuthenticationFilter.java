package com.sparta.todo.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sparta.todo.domain.constant.MemberRole;
import com.sparta.todo.dto.request.LoginRequest;
import com.sparta.todo.dto.response.LoginResponse;
import com.sparta.todo.dto.response.ErrorResponse;
import com.sparta.todo.dto.response.TokenDto;
import com.sparta.todo.security.CustomUserDetails;
import com.sparta.todo.util.RedisUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

import static com.sparta.todo.exception.ErrorCode.UNAUTHORIZED_MEMBER;

@Slf4j(topic = "로그인 인증")
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final JwtProvider jwtProvider;
    private final RedisUtils redisUtils;
    private final ObjectMapper objectMapper;

    public JwtAuthenticationFilter(JwtProvider jwtProvider, RedisUtils redisUtils, ObjectMapper objectMapper) {
        this.jwtProvider = jwtProvider;
        this.redisUtils = redisUtils;
        this.objectMapper = objectMapper;
        setFilterProcessesUrl("/api/v1/auth/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            LoginRequest loginRequest = new ObjectMapper().readValue(request.getInputStream(), LoginRequest.class);

            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.username(),
                            loginRequest.password(),
                            null
                    )
            );
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        log.info("로그인 인증 성공");
        String username = ((CustomUserDetails) authentication.getPrincipal()).getUsername();
        MemberRole role = ((CustomUserDetails) authentication.getPrincipal()).getMemberDto().role();

        TokenDto tokenDto = jwtProvider.createToken(username, role);
        jwtProvider.setTokenResponse(tokenDto, response);
        redisUtils.saveKey("RefreshToken:" + username, 24 * 60, tokenDto.refreshToken());
        setResponseConfig(response);
        objectMapper.writeValue(response.getWriter(), LoginResponse.of());
    }


    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        log.info("로그인 인증 실패");
        setResponseConfig(response);
        objectMapper
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .writeValue(response.getWriter(),
                        ErrorResponse.builder()
                                .status(UNAUTHORIZED_MEMBER.getHttpStatus().value())
                                .name(UNAUTHORIZED_MEMBER.name())
                                .message(UNAUTHORIZED_MEMBER.getDetail())
                                .build()
                );
    }

    private void setResponseConfig(HttpServletResponse response) {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("utf-8");
    }
}
