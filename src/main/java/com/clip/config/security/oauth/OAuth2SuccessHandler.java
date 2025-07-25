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

        ResponseCookie accessCookie = createAccessTokenCookie(accessToken, request);
        ResponseCookie refreshCookie = createRefreshTokenCookie(refreshToken, request);

        response.addHeader("Set-Cookie", accessCookie.toString());
        response.addHeader("Set-Cookie", refreshCookie.toString());


        // 리다이렉트 분기
        String redirectUrl = (String) request.getSession().getAttribute("redirect_uri");
        if (redirectUrl == null) {
            redirectUrl = "https://www.clipin.store";
        }

        response.sendRedirect(redirectUrl);
    }

    private ResponseCookie createAccessTokenCookie(String accessToken, HttpServletRequest request) {
        boolean isDev = isDevEnvironment(request);
        String domain = isDev ? "local.clipin.store" : "clipin.store";

        return ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .domain(domain)
                .maxAge(60 * 15)
                .build();
    }

    private ResponseCookie createRefreshTokenCookie(String refreshToken, HttpServletRequest request) {
        boolean isDev = isDevEnvironment(request);
        String domain = isDev ? "local.clipin.store" : "clipin.store";

        return ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .domain(domain)
                .maxAge(60 * 60 * 24 * 7)
                .build();
    }

    private boolean isDevEnvironment(HttpServletRequest request) {
        String host = request.getServerName();
        return host.contains("localhost") || host.contains("local.");
    }
}

