package com.mild.andyou.config.filter;

import com.mild.andyou.domain.user.User;
import com.mild.andyou.domain.user.UserRepository;
import com.mild.andyou.utils.JwtTokenUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.security.sasl.AuthenticationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final String jwtSecret;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain
    ) throws ServletException, IOException {

        String token = null;
        try {
            token = JwtTokenUtils.resolveToken(request);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if(token != null) {
            if(JwtTokenUtils.isTokenExpired(jwtSecret, token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token has expired");
                return;
            }
            String id = JwtTokenUtils.getAudience(jwtSecret, token);
            Optional<User> userOpt = userRepository.findById(Long.valueOf(id));
            userOpt.ifPresent(user -> UserContextHolder.setUserContext(new UserContext(user)));
        }

        filterChain.doFilter(request, response);

        UserContextHolder.clear();


    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        List<String> shouldNotFilterList = new ArrayList<>();
        shouldNotFilterList.add("/health");

        return shouldNotFilterList.contains(request.getRequestURI());
    }

    @Override
    public void destroy() {
        UserContextHolder.clear();
    }
}