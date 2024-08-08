package journeybuddy.spring.config.OAuth2;
/*
import io.swagger.models.auth.OAuth2Definition;
import journeybuddy.spring.config.JWT.CustomUserDetails;
import journeybuddy.spring.converter.user.UserUpdateConverter;
import journeybuddy.spring.domain.user.User;
import journeybuddy.spring.repository.user.UserRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;

@RequiredArgsConstructor
@Setter
@Service
@Slf4j
@Getter
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    //DB에서 찾아오는 메소드
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        try {
            log.info("loadUser method called");
            OAuth2User oAuth2User = super.loadUser(userRequest);

            String registrationId = userRequest.getClientRegistration().getRegistrationId();
            log.info("registrationId: {}", registrationId);

            OAuth2UserAttribute oAuth2UserAttribute = OAuth2UserAttribute.of(registrationId, oAuth2User.getAttributes());
            log.info("getAttributes(): {}", oAuth2User.getAttributes());

            User user = getOrSave(oAuth2UserAttribute);

            OAuth2UserDetails oAuth2UserDetails = new OAuth2UserDetails(user, oAuth2User.getAttributes());

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    oAuth2UserDetails,
                    null,
                    oAuth2UserDetails.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            return oAuth2UserDetails;

        } catch (Exception e) {
            log.error("Error in loadUser: ", e);
            throw new OAuth2AuthenticationException("Error occurred while loading user");
        }
    }

    private User getOrSave(OAuth2UserAttribute oAuth2UserAttribute) {
        // 이메일로 사용자 찾기
        return userRepository.findByEmail(oAuth2UserAttribute.getEmail())
                .orElseGet(() -> userRepository.save(oAuth2UserAttribute.toEntity()));
    }
}



*/


