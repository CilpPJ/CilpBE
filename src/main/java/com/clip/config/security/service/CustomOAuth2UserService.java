package com.clip.config.security.service;

import com.clip.config.security.CustomUserDetails;
import com.clip.config.security.oauth.OAuthAttributes;
import com.clip.entity.User;
import com.clip.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException{

        // 로그인 하는 유저 정보 가져오기
        OAuth2User oAuth2User = new DefaultOAuth2UserService()
                .loadUser(request);

        String provider = request.getClientRegistration().getRegistrationId();
        OAuthAttributes attributes = OAuthAttributes.of(provider, oAuth2User.getAttributes());

        // 우리 서비스에서 사용할 userId 만들기
        String userId = attributes.getProvider() + "_" + attributes.getProviderId();

        // DB에서 유저 조회, 없으면 새로 저장
        User user = userRepository.findByUserId(userId).orElseGet(() -> {
            return userRepository.save(User.builder()
                    .userId(userId)
                    .provider(attributes.getProvider())
                    .providerId(attributes.getProviderId())
                    .nickName(attributes.getNickName())
                    .password("")  // 소셜 로그인은 비번 없음
                    .build());
        });

        // 6. 우리가 만든 CustomUserDetails로 감싸기
        return new CustomUserDetails(user);
    }
}
