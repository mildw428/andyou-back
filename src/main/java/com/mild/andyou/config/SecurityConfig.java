package com.mild.andyou.config;

import com.mild.andyou.config.filter.JwtFilter;
import com.mild.andyou.config.properties.JwtProperties;
import com.mild.andyou.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final ConfigurableEnvironment environment;

    private final JwtProperties jwtProperties;
    private final UserRepository userRepository;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/health",
                                "/robots.txt",
                                "/swagger-ui/**",
                                "/swagger-resources/**",
                                "/v3/api-docs/**"
                        ).permitAll()
                        .anyRequest().permitAll()
                )
                .addFilterBefore(new JwtFilter(jwtProperties, userRepository), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000",
                "http://golrajo.com",
                "https://golrajo.com",
                "http://www.golajo.com",
                "https://www.golajo.com"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
//        config.setAllowCredentials(true); // 프론트에서 credentials: 'include' 사용 시 필수
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
