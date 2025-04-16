package com.mild.andyou.config.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "gpt")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class GptKeyProperty {

    private String apiKey;

}
