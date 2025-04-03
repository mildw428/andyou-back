package com.mild.andyou.controller.auth;


import com.mild.andyou.application.auth.KakaoAuthService;
import com.mild.andyou.controller.auth.rqrs.KakaoLoginRq;
import com.mild.andyou.controller.user.rqrs.KakaoRefreshRq;
import com.mild.andyou.controller.user.rqrs.TokenRs;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final KakaoAuthService kakaoAuthService;

    @PostMapping("/kakao")
    public ResponseEntity<TokenRs> kakaoLogin(@RequestBody KakaoLoginRq rq) {
        return ResponseEntity.ok(kakaoAuthService.kakaoLogin(rq));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenRs> kakaoRefresh(@RequestBody KakaoRefreshRq rq) {
        TokenRs rs = kakaoAuthService.kakaoRefresh(rq);
        // TODO 임시처리
        if(rs == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(kakaoAuthService.kakaoRefresh(rq));
    }
}
