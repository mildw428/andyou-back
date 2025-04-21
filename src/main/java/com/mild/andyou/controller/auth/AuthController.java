package com.mild.andyou.controller.auth;


import com.mild.andyou.application.auth.KakaoAuthService;
import com.mild.andyou.application.auth.NaverAuthService;
import com.mild.andyou.controller.auth.rqrs.KakaoLoginRq;
import com.mild.andyou.controller.auth.rqrs.NaverLoginRq;
import com.mild.andyou.controller.auth.rqrs.SignupRq;
import com.mild.andyou.controller.auth.rqrs.SignupRs;
import com.mild.andyou.controller.user.rqrs.RefreshRq;
import com.mild.andyou.controller.user.rqrs.TokenRs;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final KakaoAuthService kakaoAuthService;
    private final NaverAuthService naverAuthService;

    @PostMapping("/kakao")
    public ResponseEntity<TokenRs> kakaoLogin(@RequestBody KakaoLoginRq rq) {
        return ResponseEntity.ok(kakaoAuthService.kakaoLogin(rq));
    }

    @PostMapping("/naver")
    public ResponseEntity<TokenRs> naverLogin(@RequestBody NaverLoginRq rq) {
        TokenRs tokenRs = naverAuthService.naverLogin(rq);
        return ResponseEntity.ok(tokenRs);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenRs> refresh(@RequestBody RefreshRq rq) {
        TokenRs rs = kakaoAuthService.refresh(rq);
        // TODO 임시처리
        if (rs == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(kakaoAuthService.refresh(rq));
    }

    @PostMapping("/signup/complete")
    public ResponseEntity<TokenRs> Signup(@RequestBody SignupRq rq) {
        return ResponseEntity.ok(naverAuthService.singUp(rq));
    }

}
