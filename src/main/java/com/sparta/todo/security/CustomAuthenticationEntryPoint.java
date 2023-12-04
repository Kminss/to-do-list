package com.sparta.todo.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sparta.todo.dto.response.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Generated;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

import static com.sparta.todo.exception.ErrorCode.INVALID_AUTH_TOKEN;
@Generated
@Slf4j(topic = "Jwt 검증 실패")
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;

    public CustomAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.info("로그인 인증 실패");
        setResponseConfig(response);
        objectMapper
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .writeValue(response.getWriter(),
                        ErrorResponse.builder()
                                .status(INVALID_AUTH_TOKEN.getHttpStatus().value())
                                .name(INVALID_AUTH_TOKEN.name())
                                .message(INVALID_AUTH_TOKEN.getDetail())
                                .build()
                );
    }

    private void setResponseConfig(HttpServletResponse response) {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("utf-8");
        response.setStatus(INVALID_AUTH_TOKEN.getHttpStatus().value());
    }
}
