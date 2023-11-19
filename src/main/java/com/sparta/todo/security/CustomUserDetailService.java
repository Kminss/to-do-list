package com.sparta.todo.security;

import com.sparta.todo.dto.MemberDto;
import com.sparta.todo.repository.MemberRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailService implements UserDetailsService {
    private final MemberRepository memberRepository;

    public CustomUserDetailService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        MemberDto memberDto = memberRepository.findByUsername(username)
                .map(MemberDto::from)
                .orElseThrow(() -> new UsernameNotFoundException("회원을 찾을 수 없습니다. " + username));

        return new CustomUserDetails(memberDto);
    }
}
