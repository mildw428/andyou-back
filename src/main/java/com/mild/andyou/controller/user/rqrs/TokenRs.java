package com.mild.andyou.controller.user.rqrs;

import com.mild.andyou.utils.TokenInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
public class TokenRs {
    private String accessToken;
    private Date accessTokenExp;
    private String refreshToken;
    private Date refreshTokenExp;

    public TokenRs(TokenInfo tokenInfo) {
        this.accessToken = tokenInfo.getAccessToken();
        this.accessTokenExp = tokenInfo.getAccessTokenExp();
        this.refreshToken = tokenInfo.getRefreshToken();
        this.refreshTokenExp = tokenInfo.getRefreshTokenExp();
    }
}
