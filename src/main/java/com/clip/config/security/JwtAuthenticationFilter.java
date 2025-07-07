package com.clip.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        System.out.println("[JwtFilter] 실행됨: " + request.getRequestURI());

        // 1. 쿠키에서 accessToken 꺼내기
        String jwt = extractTokenFromCookies(request);
        if (jwt == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. 토큰에서 userId 추출
        String userId = jwtService.extractUserId(jwt);

        // 3. 인증 안 되어있으면 인증 시도
        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            System.out.println("[JwtFilter] 인증 진행 시작 - SecurityContext 비어 있음");

            UserDetails userDetails = userDetailsService.loadUserByUsername(userId);

            if (jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails.getUsername(), null, userDetails.getAuthorities());

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);

                System.out.println("[JwtFilter] SecurityContextHolder에 인증 객체 주입 완료");
            } else {
                System.out.println("[JwtFilter] 유효하지 않은 토큰");
            }
        } else {
            System.out.println("[JwtFilter] 인증 이미 되어있음 또는 userId 없음");
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
}

