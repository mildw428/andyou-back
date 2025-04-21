package com.mild.andyou.config.filter;

import com.mild.andyou.config.properties.JwtProperties;
import com.mild.andyou.domain.user.User;
import com.mild.andyou.domain.user.UserRepository;
import com.mild.andyou.utils.JwtTokenUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtProperties jwtProperties;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain
    ) throws ServletException, IOException {

        String ip = request.getHeader("X-Client-IP");

        if(ip != null) {
            UserContextHolder.setUserContext(new UserContext(ip));
        }

        String token = null;
        try {
            token = JwtTokenUtils.resolveToken(request);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if(token != null) {
            if(JwtTokenUtils.isTokenExpired(jwtProperties.getSecret(), token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token has expired");
                return;
            }
            String id = JwtTokenUtils.getAudience(jwtProperties.getSecret(), token);
            Optional<User> userOpt = userRepository.findById(Long.valueOf(id));
            if (userOpt.isPresent()) {
                UserContextHolder.setUserContext(new UserContext(userOpt.get(), ip));
            }
        }

        filterChain.doFilter(request, response);

        UserContextHolder.clear();

    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        List<String> shouldNotFilterList = new ArrayList<>();
        shouldNotFilterList.add("/health");
        shouldNotFilterList.add("/api/auth/kakao");
        shouldNotFilterList.add("/api/auth/naver");
        shouldNotFilterList.add("/api/auth/signup/complete");

        return shouldNotFilterList.contains(request.getRequestURI());
    }

    @Override
    public void destroy() {
        UserContextHolder.clear();
    }
}