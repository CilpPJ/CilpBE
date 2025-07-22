package com.clip.config.security.service;

import com.clip.config.exception.CustomException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    private final long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 15; // 15분
    private final long REFRESH_TOKEN_EXPIRATION = 1000 * 60 * 60 * 24 * 7; // 7일

    // Access 토큰 생성
    public String generateAccessToken(UserDetails userDetails) {
        return generateToken(userDetails.getUsername(), ACCESS_TOKEN_EXPIRATION);
    }

    // Refresh 토큰 생성
    public String generateRefreshToken(UserDetails userDetails) {
        return generateToken(userDetails.getUsername(), REFRESH_TOKEN_EXPIRATION);
    }

    private String generateToken(String userId, long expiration) {
        return Jwts.builder()
                .setSubject(userId) // 식별자
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    ////////////////////////////////////////////////////////////

    // 토큰 유효성 검증
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String userId = extractUserId(token);
        return (userId.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // 리프레시 토큰 유효성 검증 => 재발급용
    public boolean isRefreshTokenValid(String token) {
        try {
            Claims claims = extractAllClaims(token); // 시크릿 키로 파싱
            Date expiration = claims.getExpiration();
            return !expiration.before(new Date()); // 만료 안 됐으면 true
        } catch (Exception e) {
            return false;
        }
    }

    public String extractUserId(String token) {
        try {
            return extractAllClaims(token).getSubject();
        } catch (MalformedJwtException e) {
            throw new CustomException("JWT_001", "잘못된 JWT 형식입니다.");
        } catch (ExpiredJwtException e) {
            throw new CustomException("JWT_002", "JWT가 만료되었습니다.");
        } catch (SignatureException e) {
            throw new CustomException("JWT_003", "JWT 서명이 유효하지 않습니다.");
        } catch (Exception e) {
            throw new CustomException("JWT_999", "JWT 파싱 중 알 수 없는 오류가 발생했습니다.");
        }
    }


    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    // JWT에서 서명키로 접속해서 Claims(내용 전체)를 추출
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
