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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
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
                        || uri.startsWith("/h2-console")
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

                    // accessToken 재발급
                    String newAccessToken = jwtService.generateAccessToken(userDetails);
                    Cookie newCookie = new Cookie("accessToken", newAccessToken);
                    newCookie.setHttpOnly(true);
                    newCookie.setSecure(true);
                    newCookie.setPath("/");
                    newCookie.setMaxAge(60 * 15); // 15분
                    response.addCookie(newCookie);

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

    // 쿠키에서 accessToken 추출하는 유틸
    private String extractTokenFromCookies(HttpServletRequest request) {
        if (request.getCookies() == null) return null;

        return Arrays.stream(request.getCookies())
                .filter(cookie -> "accessToken".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }

    // 쿠키에서 refreshToken 추출하는 유틸
    private String extractRefreshTokenFromCookies(HttpServletRequest request) {
        if (request.getCookies() == null) return null;

        return Arrays.stream(request.getCookies())
                .filter(cookie -> "refreshToken".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }
}

