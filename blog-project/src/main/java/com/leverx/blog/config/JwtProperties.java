package com.leverx.blog.config;

public class JwtProperties {
    public static final String SECRET = "NotASecretAnymore";
    public static final int EXPIRATION_TIME = 86400000;
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";

}
