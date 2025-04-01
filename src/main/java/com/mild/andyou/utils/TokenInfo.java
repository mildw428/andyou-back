package com.mild.andyou.utils;

import io.jsonwebtoken.Claims;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Getter
public class TokenInfo {

    private String accessToken;
    private Date accessTokenExp;
    private String refreshToken;
    private Date refreshTokenExp;

    public TokenInfo(String token, Date accessTokenExp, String refreshToken, Date refreshTokenExp) {
        this.accessToken = token;
        this.accessTokenExp = accessTokenExp;
        this.refreshToken = refreshToken;
        this.refreshTokenExp = refreshTokenExp;
    }

}