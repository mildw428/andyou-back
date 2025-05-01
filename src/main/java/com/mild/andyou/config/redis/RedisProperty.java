package com.mild.andyou.config.redis;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter @Setter
@ConfigurationProperties("spring.redis")
public class RedisProperty {

    private String host;
    private Integer port;
    private Integer database;
    private String password;

}