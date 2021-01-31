package com.leverx.blog.repository;

import com.leverx.blog.model.RedisCode;

public interface RedisCodeRepository {

    void save(RedisCode redisCode);

    RedisCode findByCode(String code);

    void deleteByCode(String code);
}
