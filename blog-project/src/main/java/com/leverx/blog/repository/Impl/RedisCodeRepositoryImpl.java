package com.leverx.blog.repository.Impl;

import com.leverx.blog.model.RedisCode;
import com.leverx.blog.repository.RedisCodeRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
public class RedisCodeRepositoryImpl implements RedisCodeRepository {

    private static final String HASH_KEY = "RedisCode";

    private RedisTemplate redisTemplate;

    public RedisCodeRepositoryImpl(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void save(RedisCode redisCode) {
        redisTemplate.expire(HASH_KEY, Duration.ofDays(1));
        redisTemplate.opsForHash().put(HASH_KEY, redisCode.getCode(), redisCode);
    }

    @Override
    public RedisCode findByCode(String code) {
        return (RedisCode) redisTemplate.opsForHash().get(HASH_KEY, code);
    }

    @Override
    public void deleteByCode(String code) {
        redisTemplate.opsForHash().delete(HASH_KEY, code);
    }
}
