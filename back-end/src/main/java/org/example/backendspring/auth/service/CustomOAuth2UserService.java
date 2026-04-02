package org.example.backendspring.auth.service;

import org.example.backendspring.auth.entity.User;
import org.example.backendspring.auth.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * Google OAuth2 사용자 정보를 처리하는 서비스.
 */
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * OAuth2 사용자 정보를 로드하고, 신규 사용자는 DB에 저장한다.
     */
    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        Map<String, Object> attributes = oAuth2User.getAttributes();

        String oauthProvider = "google";
        String oauthSubject = (String) attributes.get("sub");
        String email = (String) attributes.get("email");
        String nickname = (String) attributes.get("name");
        String profileImage = (String) attributes.get("picture");

        User user = userRepository
                .findByOauthProviderAndOauthSubject(oauthProvider, oauthSubject)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setOauthProvider(oauthProvider);
                    newUser.setOauthSubject(oauthSubject);
                    newUser.setEmail(email);
                    newUser.setNickname(nickname != null ? nickname : email);
                    newUser.setProfileImageUrl(profileImage);
                    return userRepository.save(newUser);
                });

        boolean dirty = false;
        if (nickname != null && !nickname.equals(user.getNickname())) {
            user.setNickname(nickname);
            dirty = true;
        }
        if (profileImage != null && !profileImage.equals(user.getProfileImageUrl())) {
            user.setProfileImageUrl(profileImage);
            dirty = true;
        }
        if (dirty) {
            userRepository.save(user);
        }

        Map<String, Object> enrichedAttributes = new HashMap<>(attributes);
        enrichedAttributes.put("userId", user.getId());

        String userNameAttributeName = userRequest
                .getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        return new DefaultOAuth2User(
                oAuth2User.getAuthorities(),
                enrichedAttributes,
                userNameAttributeName
        );
    }
}
