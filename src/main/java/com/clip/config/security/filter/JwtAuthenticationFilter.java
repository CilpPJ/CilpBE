package com.clip.config.security.filter;

import com.clip.config.exception.CustomException;
import com.clip.config.exception.enums.JwtErrorCode;
import com.clip.config.security.service.CustomUserDetailsService;
import com.clip.config.security.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();

        if (
                uri.startsWith("/api/auth/") || uri.startsWith("/swagger-ui/")
                        || uri.startsWith("/swagger-ui.html") || uri.startsWith("/v3/api-docs")
                        || uri.startsWith("/test-oauth.html") || uri.startsWith("/h2-console")
        ) {
            filterChain.doFilter(request, response);
            return;
        }

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            System.out.println("[JwtFilter] 인증 진행 시작 - SecurityContext 비어 있음");

            String accessToken = extractTokenFromCookies(request);
            String userId = jwtService.extractUserId(accessToken);

            if (userId != null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(userId);

                if (jwtService.isTokenValid(accessToken, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities()
                            );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    System.out.println("[JwtFilter] accessToken 인증 성공");
                    filterChain.doFilter(request, response);
                    return;
                } else {
                    throw new CustomException(JwtErrorCode.INVALID_SIGNATURE.getCode(), JwtErrorCode.INVALID_SIGNATURE.getMessage());
                }

            } else {
                // accessToken 없음 → refresh 시도
                String refreshToken = extractRefreshTokenFromCookies(request);

                if (refreshToken != null && jwtService.isRefreshTokenValid(refreshToken)) {
                    userId = jwtService.extractUserId(refreshToken);
                    UserDetails userDetails = userDetailsService.loadUserByUsername(userId);

                    String newAccessToken = jwtService.generateAccessToken(userDetails);

                    // 통일된 쿠키 설정 로직 호출
                    addAccessTokenCookie(response, newAccessToken);

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities()
                            );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    System.out.println("[JwtFilter] refreshToken 인증 성공 → accessToken 재발급");
                } else {
                    throw new CustomException(JwtErrorCode.EMPTY_TOKEN.getCode(), JwtErrorCode.EMPTY_TOKEN.getMessage());
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractTokenFromCookies(HttpServletRequest request) {
        if (request.getCookies() == null) {
            throw new AuthenticationCredentialsNotFoundException("로그인 정보가 없습니다.");
        }

        return Arrays.stream(request.getCookies())
                .filter(cookie -> "accessToken".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }

    private String extractRefreshTokenFromCookies(HttpServletRequest request) {
        if (request.getCookies() == null) {
            throw new AuthenticationCredentialsNotFoundException("로그인 정보가 없습니다.");
        }

        return Arrays.stream(request.getCookies())
                .filter(cookie -> "refreshToken".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }

    private void addAccessTokenCookie(HttpServletResponse response, String token) {
        ResponseCookie cookie = ResponseCookie.from("accessToken", token)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(Duration.ofMinutes(15))
                .build();

        response.setHeader("Set-Cookie", cookie.toString());
    }

}

