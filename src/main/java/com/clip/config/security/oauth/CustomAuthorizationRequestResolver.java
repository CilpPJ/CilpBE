package com.clip.config.security.oauth;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CustomAuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

    private final ClientRegistrationRepository clientRegistrationRepository;

    private final String authorizationRequestBaseUri = "/oauth2/authorization"; // 기본 URI 설정

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        OAuth2AuthorizationRequestResolver defaultResolver =
                new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository, authorizationRequestBaseUri);

        OAuth2AuthorizationRequest requestToSend = defaultResolver.resolve(request);
        return customizeRedirectUri(request, requestToSend);
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        OAuth2AuthorizationRequestResolver defaultResolver =
                new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository, authorizationRequestBaseUri);

        OAuth2AuthorizationRequest requestToSend = defaultResolver.resolve(request, clientRegistrationId);
        return customizeRedirectUri(request, requestToSend);
    }

    private OAuth2AuthorizationRequest customizeRedirectUri(HttpServletRequest request, OAuth2AuthorizationRequest req) {
        if (req == null) return null;

        String redirectUri = request.getParameter("redirect_uri");
        if (redirectUri != null) {
            request.getSession().setAttribute("redirect_uri", redirectUri);
        }

        return req;
    }
}

