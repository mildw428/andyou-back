package com.mild.andyou.application.auth;

import com.mild.andyou.application.auth.dto.NaverTokenResponse;
import com.mild.andyou.application.auth.dto.NaverUserResponse;
import com.mild.andyou.config.filter.UserContextHolder;
import com.mild.andyou.config.properties.JwtProperties;
import com.mild.andyou.config.properties.NaverProperties;
import com.mild.andyou.controller.auth.rqrs.NaverLoginRq;
import com.mild.andyou.controller.auth.rqrs.SignupRq;
import com.mild.andyou.controller.auth.rqrs.SignupRs;
import com.mild.andyou.controller.user.rqrs.TokenRs;
import com.mild.andyou.domain.user.User;
import com.mild.andyou.domain.user.UserRepository;
import com.mild.andyou.utils.JwtTokenUtils;
import com.mild.andyou.utils.TokenInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Transactional(readOnly = true)
@Slf4j
@Service
@RequiredArgsConstructor
public class NaverAuthService {

    private final NaverProperties naverProperties;
    private final JwtProperties jwtProperties;

    private final WebClient webClient = WebClient.create();
    private final UserRepository userRepository;

    @Transactional
    public TokenRs naverLogin(NaverLoginRq rq) {

        NaverTokenResponse tokenResponse = getNaverTokenResponse(rq);

        if (tokenResponse == null || tokenResponse.getAccessToken() == null) {
            throw new RuntimeException("네이버 토큰 발급 실패");
        }

        NaverUserResponse userResponse = getNaverUserResponse(tokenResponse);

        if (userResponse == null || userResponse.getResponse() == null || userResponse.getResponse().getId() == null) {
            throw new RuntimeException("네이버 사용자 정보 조회 실패");
        }

        User user = userRepository.findBySocialTypeAndSocialId(User.SocialType.NAVER, userResponse.getResponse().getId())
                .orElseGet(() -> userRepository.save(new User(User.SocialType.NAVER, userResponse.getResponse().getId())));

        String birthYear = userResponse.getResponse().getBirthyear();
        String gender = userResponse.getResponse().getGender();

        if(user.isNewUser()) {
            return new TokenRs(user.getId(), "비회원", birthYear, gender);
        }

        if (user.isSuspended()) {
            throw new RuntimeException("비활성 계정");
        }

        TokenInfo tokenInfo = JwtTokenUtils.createToken(jwtProperties.getSecret(), user.getId().toString());
        user.updateRefresh(tokenInfo.getRefreshToken(), tokenInfo.getRefreshTokenExp());

        return new TokenRs(tokenInfo, user.getId(), user.getNickname(), user.getBirthYear(), user.getGender());
    }

    private NaverTokenResponse getNaverTokenResponse(NaverLoginRq rq) {
        NaverTokenResponse tokenResponse = webClient.post()
                .uri("https://nid.naver.com/oauth2.0/token")
                .header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
                .body(BodyInserters
                        .fromFormData("grant_type", "authorization_code")
                        .with("client_id", naverProperties.getClientId())
                        .with("client_secret", naverProperties.getClientSecret())
                        .with("redirect_uri", naverProperties.getRedirectUri())
                        .with("code", rq.getCode())
                        .with("state", rq.getState())
                )
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> {
                    log.error("4xx Error - Status: {}", response.statusCode());
                    return Mono.error(new RuntimeException("네이버 4xx 오류"));
                })
                .onStatus(HttpStatusCode::is5xxServerError, response -> {
                    log.error("5xx Error - Status: {}", response.statusCode());
                    return Mono.error(new RuntimeException("네이버 서버 오류"));
                })
                .bodyToMono(NaverTokenResponse.class)
                .block();
        return tokenResponse;
    }

    private NaverUserResponse getNaverUserResponse(NaverTokenResponse tokenResponse) {
        String naverAccessToken = tokenResponse.getAccessToken();

        NaverUserResponse userResponse = webClient.get()
                .uri("https://openapi.naver.com/v1/nid/me")
                .header("Authorization", "Bearer " + naverAccessToken)
                .retrieve()
                .bodyToMono(NaverUserResponse.class)
                .block();
        return userResponse;
    }

    @Transactional
    public TokenRs singUp(SignupRq rq) {
        User user = userRepository.findById(rq.getId()).orElseThrow();
        user.updateNickname("사용자"+user.getId());
        user.singUp(rq.getBirthYear(), rq.getGender());
        TokenInfo tokenInfo = JwtTokenUtils.createToken(jwtProperties.getSecret(), user.getId().toString());
        user.updateRefresh(tokenInfo.getRefreshToken(), tokenInfo.getRefreshTokenExp());

        return new TokenRs(tokenInfo, user.getId(), user.getNickname(), user.getBirthYear(), user.getGender());

    }
}