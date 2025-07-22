package com.clip.config.security.oauth;

import com.clip.config.exception.CustomException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
public class OAuthAttributes {
    private String provider;
    private String providerId;
    private String email;
    private String nickName;
    private Map<String, Object> attributes; // 원본 응답 전체 저장

    public static OAuthAttributes of(String provider, Map<String, Object> attributes){
        if ("google".equals(provider)) {
            return ofGoogle(provider, attributes);
        } else if ("kakao".equals(provider)) {
            return ofKakao(provider, attributes);
        } else {
            throw new CustomException("SOCIAL_LOGIN_NOT_FOUND", "해당 소셜 로그인은 지원하지 않습니다");
        }
    }

    private static OAuthAttributes ofGoogle(String provider, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .provider(provider)
                .providerId((String) attributes.get("sub"))         // 구글 고유 ID
                .email((String) attributes.get("email"))            // 이메일
                .nickName((String) attributes.get("name"))          // 사용자 이름
                .attributes(attributes)
                .build();
    }

    private static OAuthAttributes ofKakao(String provider, Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        return OAuthAttributes.builder()
                .provider(provider)
                .providerId(attributes.get("id").toString())                    // 카카오 고유 ID
                .email((String) kakaoAccount.get("email"))                     // 이메일
                .nickName((String) profile.get("nickname"))                    // 닉네임
                .attributes(attributes)
                .build();
    }

}
