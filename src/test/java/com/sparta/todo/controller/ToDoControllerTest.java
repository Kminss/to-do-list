package com.sparta.todo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.todo.config.PasswordEncoderConfig;
import com.sparta.todo.domain.constant.MemberRole;
import com.sparta.todo.dto.MemberDto;
import com.sparta.todo.dto.request.CreateToDoRequest;
import com.sparta.todo.dto.response.ToDoResponse;
import com.sparta.todo.security.CustomUserDetails;
import com.sparta.todo.service.ToDoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@Import(PasswordEncoderConfig.class)
@WebMvcTest(ToDoController.class)
@DisplayName("할 일 API 컨트롤러 테스트")
class ToDoControllerTest {

    private final MockMvc mvc;
    private final ObjectMapper objectMapper;
    private final PasswordEncoder passwordEncoder;
    @MockBean
    private ToDoService toDoService;

    ToDoControllerTest(
            @Autowired MockMvc mvc,
            @Autowired ObjectMapper objectMapper,
            @Autowired PasswordEncoder passwordEncoder) {
        this.mvc = mvc;
        this.objectMapper = objectMapper;
        this.passwordEncoder = passwordEncoder;
    }
    @DisplayName("[Controller][POST] 할일 생성 성공")
    @WithMockUser(username = "username", password = "testpassword", authorities = {"ROLE_USER"})
    @Test
    void givenToDoRequest_whenRequesting_thenSuccess () throws Exception {
        //Given
        MemberDto memberDto = MemberDto.builder()
                .id(1L)
                .username("username")
                .password("password")
                .role(MemberRole.USER)
                .build();
        CustomUserDetails userDetails = new CustomUserDetails(memberDto);
        CreateToDoRequest toDoRequest = new CreateToDoRequest("test title", "test content");

        given(toDoService.createToDo(toDoRequest, memberDto))
                .willReturn(ToDoResponse.from(toDoRequest.toEntity(memberDto.toEntity(passwordEncoder))));

        //When
        ResultActions actions = mvc.perform(
                post("/api/v1/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(toDoRequest))
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .with(user(userDetails))
        );

        //Then
        actions
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(toDoRequest.title()))
                .andExpect(jsonPath("$.content").value(toDoRequest.content()))
                .andExpect(jsonPath("$.username").value(memberDto.username()));
    }

}