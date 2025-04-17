package com.mild.andyou.utils;

import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class JwtTokenUtils {

    public static TokenInfo createToken(String jwtSecret, String accountId) {
        Date iat = new Date();
        Map<String, Object> claims = new HashMap<>();

        Date accessTokenExp = getAccessExp(iat);
        String refreshToken = UUID.randomUUID().toString();
        Date refreshTokenExp = getRefreshExp(iat);

        String token = Jwts.builder()
                .setAudience(accountId)
                .addClaims(claims)
                .setIssuedAt(iat)
                .setExpiration(accessTokenExp)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();

        return new TokenInfo(token, accessTokenExp, refreshToken, refreshTokenExp);
    }

    private static Date getAccessExp(Date iat) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(iat);
        calendar.add(Calendar.DAY_OF_MONTH, 2);
        return calendar.getTime();
    }

    private static Date getRefreshExp(Date iat) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(iat);
        calendar.add(Calendar.DAY_OF_MONTH, 7);
        return calendar.getTime();
    }

    public static String resolveToken(HttpServletRequest request) throws Exception {
        String authorization = request.getHeader("Authorization");

        if (Objects.isNull(authorization)) {
            authorization = request.getParameter("Authorization");
        }

        if (Objects.isNull(authorization) || !authorization.startsWith("Bearer")) {
            return null;
        }

        return URLDecoder.decode(authorization, StandardCharsets.UTF_8.name()).substring("Bearer".length()).trim();
    }

    public static String getAudience(String jwtSecret, String token) {
        Claims claims = getClaims(jwtSecret, token);
        return claims.getAudience();
    }

    public static Claims getClaims(String jwtSecret, String token) {

        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
    }

    public static boolean isTokenExpired(String jwtSecret, String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(token)
                    .getBody();
            Date expiration = claims.getExpiration();
            return expiration.before(new Date());
        } catch (SignatureException | ExpiredJwtException e) {
            return true;
        }
    }

}