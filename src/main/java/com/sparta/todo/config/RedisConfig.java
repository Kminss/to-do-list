package com.sparta.todo.config;

import com.sparta.todo.util.RedisUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(RedisProperty.class)
public class RedisConfig {
    private final RedisProperty redisProperty;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(redisProperty.getHost(), redisProperty.getPort());
    }


    @Bean
    public StringRedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory) {

        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);

        return template;

    }


    // 필수는 아니고 제 회사에서 쓰는 형태의 설정
    @Bean
    public RedisUtils redisComponent(StringRedisTemplate stringRedisTemplate) {

        return new RedisUtils(stringRedisTemplate);

    }

}

@Getter
@Setter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "spring.data.redis")
class RedisProperty {

    private final String host;
    private final Integer port;

}