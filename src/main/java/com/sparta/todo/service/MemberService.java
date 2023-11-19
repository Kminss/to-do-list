package com.sparta.todo.service;

import com.sparta.todo.dto.request.SignupRequest;
import com.sparta.todo.exception.AlreadyExistMemberException;
import com.sparta.todo.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Transactional
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;


    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void signup(SignupRequest request) {

        if (memberRepository.existsByUsername(request.username())) {
            throw new AlreadyExistMemberException();
        }
        memberRepository.save(request.toEntity(passwordEncoder));
    }
}
