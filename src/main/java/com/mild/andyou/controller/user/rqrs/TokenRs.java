package com.mild.andyou.controller.user.rqrs;

import com.mild.andyou.controller.auth.rqrs.UserDto;
import com.mild.andyou.utils.TokenInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class TokenRs {
    private String accessToken;
    private Date accessTokenExp;
    private String refreshToken;
    private Date refreshTokenExp;
    private UserDto user;

    public TokenRs(TokenInfo tokenInfo, String nickname) {
        this.accessToken = tokenInfo.getAccessToken();
        this.accessTokenExp = tokenInfo.getAccessTokenExp();
        this.refreshToken = tokenInfo.getRefreshToken();
        this.refreshTokenExp = tokenInfo.getRefreshTokenExp();
        this.user = new UserDto(nickname);
    }
}
