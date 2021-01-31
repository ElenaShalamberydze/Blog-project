package com.leverx.blog.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("RedisCode")
public class RedisCode implements Serializable {

    @Id
    private String code;
    private String email;

}
