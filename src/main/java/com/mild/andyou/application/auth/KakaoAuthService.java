package com.mild.andyou.application.auth;

import com.mild.andyou.application.auth.dto.KakaoTokenResponse;
import com.mild.andyou.application.auth.dto.KakaoUserResponse;
import com.mild.andyou.config.properties.JwtProperties;
import com.mild.andyou.config.properties.KakaoProperties;
import com.mild.andyou.controller.auth.rqrs.KakaoLoginRq;
import com.mild.andyou.controller.user.rqrs.RefreshRq;
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

import java.util.Optional;

@Transactional(readOnly = true)
@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoAuthService {

    private final KakaoProperties kakaoProperties;
    private final JwtProperties jwtProperties;

    private final WebClient webClient = WebClient.create();
    private final UserRepository userRepository;

    @Transactional
    public TokenRs kakaoLogin(KakaoLoginRq rq) {

        KakaoTokenResponse tokenResponse = getKakaoTokenResponse(rq);

        if (tokenResponse == null || tokenResponse.getAccessToken() == null) {
            throw new RuntimeException("카카오 토큰 발급 실패");
        }

        KakaoUserResponse userResponse = getKakaoUserResponse(tokenResponse);

        if (userResponse == null || userResponse.getId() == null) {
            throw new RuntimeException("카카오 사용자 정보 조회 실패");
        }

        User user = userRepository.findBySocialTypeAndSocialId(User.SocialType.KAKAO, userResponse.getId())
                .orElseGet(() -> userRepository.save(new User(User.SocialType.KAKAO, userResponse.getId())));

        if(user.isNewUser()) {

        }

        if (user.isSuspended()) {
            throw new RuntimeException("비활성 계정");
        }

        user.updateNickname("사용자"+user.getId());

        TokenInfo tokenInfo = JwtTokenUtils.createToken(jwtProperties.getSecret(), user.getId().toString());

        user.updateRefresh(tokenInfo.getRefreshToken(), tokenInfo.getRefreshTokenExp());

        return new TokenRs(tokenInfo, user.getId(), user.getNickname(),user.getBirthYear(), user.getGender());
    }

    private KakaoTokenResponse getKakaoTokenResponse(KakaoLoginRq rq) {
        KakaoTokenResponse tokenResponse = webClient.post()
                .uri("https://kauth.kakao.com/oauth/token")
                .header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
                .body(BodyInserters
                        .fromFormData("grant_type", "authorization_code")
                        .with("client_id", kakaoProperties.getClientId())
                        .with("client_secret", kakaoProperties.getClientSecret())
                        .with("redirect_uri", kakaoProperties.getRedirectUri())
                        .with("code", rq.getCode())
                )
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> {
                    log.error("4xx Error - Status: {}", response.statusCode());
                    return Mono.error(new RuntimeException("카카오 4xx 오류"));
                })
                .onStatus(HttpStatusCode::is5xxServerError, response -> {
                    log.error("5xx Error - Status: {}", response.statusCode());
                    return Mono.error(new RuntimeException("카카오 서버 오류"));
                })
                .bodyToMono(KakaoTokenResponse.class)
                .block();
        return tokenResponse;
    }

    private KakaoUserResponse getKakaoUserResponse(KakaoTokenResponse tokenResponse) {
        String kakaoAccessToken = tokenResponse.getAccessToken();

        KakaoUserResponse userResponse = webClient.get()
                .uri("https://kapi.kakao.com/v2/user/me")
                .header("Authorization", "Bearer " + kakaoAccessToken)
                .retrieve()
                .bodyToMono(KakaoUserResponse.class)
                .block();
        return userResponse;
    }

    @Transactional
    public TokenRs refresh(RefreshRq rq) {
        String refreshToken = rq.getRefreshToken();
        Optional<User> userOpt = userRepository.findByRefreshToken(refreshToken);

        if(userOpt.isEmpty()) {
            return null;
        }

        User user = userOpt.get();

        if(user.isExp()) {
            return null;
        }

        TokenInfo tokenInfo = JwtTokenUtils.createToken(jwtProperties.getSecret(), user.getId().toString());
        user.updateRefresh(tokenInfo.getRefreshToken(), tokenInfo.getRefreshTokenExp());

        return new TokenRs(tokenInfo, user.getId(), user.getNickname(), user.getBirthYear(), user.getGender());

    }
}
