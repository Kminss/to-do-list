package com.sparta.todo.service;

import com.sparta.todo.domain.constant.MemberRole;
import com.sparta.todo.dto.response.TokenDto;
import com.sparta.todo.exception.InvalidRefreshTokenException;
import com.sparta.todo.jwt.JwtProvider;
import com.sparta.todo.util.RedisUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class AuthService {
    private final JwtProvider jwtProvider;
    private final RedisUtils redisUtils;

    public AuthService(JwtProvider jwtProvider, RedisUtils redisUtils) {
        this.jwtProvider = jwtProvider;
        this.redisUtils = redisUtils;
    }


    public void reissue(HttpServletRequest request, HttpServletResponse response) {
        String targetToken = jwtProvider.getRefreshTokenFromCookie(request);

        //토큰 검증
        jwtProvider.validateToken(targetToken);

        //토큰에서 username 추출
        Claims claims = jwtProvider.getUserInfoFromToken(targetToken);
        String username = claims.getSubject();
        MemberRole role = MemberRole.valueOf((String) claims.get("auth"));

        //redis에서 refresh token 확인
        String refreshToken = redisUtils.getKey(username);

        //refresh token 일치하는지 검증
        if (!refreshToken.equals(targetToken)) {
            throw new InvalidRefreshTokenException();
        }

        //토큰 재발급
        TokenDto tokenDto = jwtProvider.createToken(username, role);
        jwtProvider.setTokenResponse(tokenDto, response);

        //재발급된 토큰 정보 저장
        redisUtils.saveKey("RefreshToken:" + username, 24 * 60, tokenDto.refreshToken());
    }

}