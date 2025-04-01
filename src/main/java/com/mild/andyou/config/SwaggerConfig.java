package com.mild.andyou.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;  
import io.swagger.v3.oas.models.info.Info;  
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;  
import io.swagger.v3.oas.models.servers.Server;  
import lombok.RequiredArgsConstructor;
import org.springdoc.core.models.GroupedOpenApi;
import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;  
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CookieValue;  
  
import java.time.LocalDate;  
import java.time.LocalDateTime;  
import java.time.LocalTime;  
  
@Configuration  
@RequiredArgsConstructor  
public class SwaggerConfig {  

    static {  
        SpringDocUtils.getConfig()
                .replaceWithClass(LocalDateTime.class, String.class)  
                .replaceWithClass(LocalDate.class, String.class)  
                .replaceWithClass(LocalTime.class, String.class)  
                .addAnnotationsToIgnore(AuthenticationPrincipal.class, CookieValue.class);
    }  
  
    @Bean  
    public GroupedOpenApi cms() {
        return GroupedOpenApi.builder()  
                .group("그룹1")
                .pathsToMatch("/api/**")
                .build();
    }  

    @Bean  
    public OpenAPI getOpenApi() {  
        final String securitySchemeName = "bearerAuth";  
  
        return new OpenAPI()  
                .info(new Info().title("테스트 API"))
                .addServersItem(new Server().url("/"))  
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))  
                .components(new Components()  
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()  
                                .name(securitySchemeName)  
                                .type(SecurityScheme.Type.HTTP)  
                                .scheme("bearer")  
                                .bearerFormat("JWT")));  
    }

}