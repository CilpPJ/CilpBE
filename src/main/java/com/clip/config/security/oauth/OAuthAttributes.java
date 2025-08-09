package com.clip.config.security.oauth;

import com.clip.config.exception.CustomException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Getter
@Builder
@AllArgsConstructor
public class OAuthAttributes {
    private String provider;
    private String providerId;
    private String email;
    private String nickname;
    private Map<String, Object> attributes;

    public static OAuthAttributes of(String provider, Map<String, Object> attributes) {
        String id = Optional.ofNullable(provider).orElse("").trim().toLowerCase(Locale.ROOT);
        return switch (id) {
            case "google" -> ofGoogle(id, attributes);
            case "kakao"  -> ofKakao(id, attributes);
            default -> throw new CustomException("SOCIAL_LOGIN_NOT_FOUND", "해당 소셜 로그인은 지원하지 않습니다");
        };
    }

    private static OAuthAttributes ofGoogle(String provider, Map<String, Object> attrs) {
        String sub = (String) attrs.get("sub");
        if (sub == null) {
            throw new CustomException("OAUTH_ATTR_MISSING", "Google 응답에 sub가 없습니다");
        }
        String email = (String) attrs.get("email");
        String name  = (String) attrs.get("name");
        return OAuthAttributes.builder()
                .provider(provider)
                .providerId(sub)
                .email(email)            // 이메일 동의 안 주면 null일 수 있음
                .nickname(name != null ? name : ("google_" + sub))
                .attributes(attrs)
                .build();
    }

    @SuppressWarnings("unchecked")
    private static OAuthAttributes ofKakao(String provider, Map<String, Object> attrs) {
        Object idObj = attrs.get("id");
        if (idObj == null) {
            throw new CustomException("OAUTH_ATTR_MISSING", "Kakao 응답에 id가 없습니다");
        }
        String providerId = String.valueOf(idObj);

        Map<String, Object> account = (Map<String, Object>) attrs.getOrDefault("kakao_account", Map.of());
        Map<String, Object> profile = (Map<String, Object>) account.getOrDefault("profile", Map.of());

        String email = (String) account.get("email"); // 동의 안하면 null
        String nickname = Optional.ofNullable((String) profile.get("nickname"))
                .orElse("kakao_" + providerId);

        return OAuthAttributes.builder()
                .provider(provider)
                .providerId(providerId)
                .email(email)
                .nickname(nickname)
                .attributes(attrs)
                .build();
    }
}
