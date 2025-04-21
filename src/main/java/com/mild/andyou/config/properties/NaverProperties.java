package com.mild.andyou.config.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "naver")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NaverProperties {

    private String clientId;
    private String clientSecret;
    private String redirectUri;
}
