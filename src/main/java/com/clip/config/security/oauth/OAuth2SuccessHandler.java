package com.clip.config.security.oauth;

import com.clip.config.security.CustomUserDetails;
import com.clip.config.security.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        System.out.println("[OAuth2SuccessHandler] called for user: " + userDetails.getUsername());

        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        boolean isDev = isDevEnvironment(request);

        ResponseCookie accessCookie = createAccessTokenCookie(accessToken, isDev);
        ResponseCookie refreshCookie = createRefreshTokenCookie(refreshToken, isDev);

        response.addHeader("Set-Cookie", accessCookie.toString());
        response.addHeader("Set-Cookie", refreshCookie.toString());

        // 리다이렉트 분기
        String redirectUrl;
        String origin = request.getHeader("Origin"); // or referer 써도 됨

        if (origin != null && origin.contains("localhost")) {
            redirectUrl = "http://localhost:5173";
        } else {
            redirectUrl = "https://www.clipin.store";
        }

        response.sendRedirect(redirectUrl);
    }

    private ResponseCookie createAccessTokenCookie(String accessToken, boolean isDev) {
        return ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .secure(!isDev == true) // 운영환경이면 true, 개발이면 false
                .sameSite(isDev ? "Lax" : "None")
                .path("/")
                .maxAge(60 * 15)
                .build();
    }

    private ResponseCookie createRefreshTokenCookie(String refreshToken, boolean isDev) {
        return ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(!isDev == true)
                .sameSite(isDev ? "Lax" : "None")
                .path("/")
                .maxAge(60 * 60 * 24 * 7)
                .build();
    }

    private boolean isDevEnvironment(HttpServletRequest request) {
        String origin = request.getHeader("Origin");
        return origin != null && origin.contains("localhost");
    }
}

