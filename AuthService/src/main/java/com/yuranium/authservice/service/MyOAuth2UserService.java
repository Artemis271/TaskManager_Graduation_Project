package com.yuranium.authservice.service;

import com.yuranium.authservice.models.entity.UserEntity;
import com.yuranium.authservice.models.oauth2.OAuth2UserInfo;
import com.yuranium.authservice.repository.UserRepository;
import com.yuranium.authservice.service.kafka.KafkaProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
public class MyOAuth2UserService extends DefaultOAuth2UserService
{
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final RoleService roleService;

    private final KafkaProducer kafkaProducer;

    @Value("${oauth2.github-registration-id}")
    private String githubRegistrationId;

    @Value("${oauth2.github-emails-endpoint}")
    private String githubEmailsEndpoint;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException
    {
        OAuth2User oauthUser = super.loadUser(userRequest);
        if (!isGithubUser(userRequest))
            return oauthUser;

        String token = userRequest.getAccessToken().getTokenValue();
        String primaryEmail = fetchGithubPrimaryEmail(token);
        Map<String, Object> enrichedAttrs = enrichAttributes(
                oauthUser.getAttributes(), "email", primaryEmail);
        return new DefaultOAuth2User(oauthUser.getAuthorities(),
                enrichedAttrs, "login");
    }

    private boolean isGithubUser(OAuth2UserRequest request)
    {
        return githubRegistrationId.equals(
                request.getClientRegistration().getRegistrationId()
        );
    }

    private String fetchGithubPrimaryEmail(String accessToken)
    {
        RestTemplate template = new RestTemplate();
        HttpEntity<Void> request = new HttpEntity<>(createGithubHeaders(accessToken));

        ResponseEntity<List<Map<String, Object>>> response = template.exchange(
                githubEmailsEndpoint,
                HttpMethod.GET, request,
                new ParameterizedTypeReference<>() {}
        );

        return response.getBody().stream()
                .filter(m -> Boolean.TRUE.equals(m.get("primary")))
                .map(m -> (String) m.get("email"))
                .findFirst()
                .orElse(null);
    }

    private HttpHeaders createGithubHeaders(String accessToken)
    {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.set("Accept", "application/vnd.github.v3+json");
        return headers;
    }

    private Map<String, Object> enrichAttributes(
            Map<String, Object> original, String key, Object value
    )
    {
        Map<String, Object> copy = new HashMap<>(original);
        copy.put(key, value);
        return copy;
    }

    @Transactional
    public UserEntity createUser(OAuth2UserInfo userInfo)
    {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(userInfo.getUsername());
        userEntity.setName(userInfo.getFirstName());
        userEntity.setLastName(userInfo.getLastName());
        userEntity.setPassword(passwordEncoder.encode(generateRandomPassword()));
        userEntity.setEmail(userInfo.getEmail());
        userEntity.setActivity(true);
        userEntity.setRoles(new HashSet<>(Set.of(roleService.getRole(1))));
        roleService.saveAll(userEntity.getRoles());

        userEntity = userRepository.save(userEntity);
        kafkaProducer.sendCreateUserEvent(userEntity);
        return userEntity;
    }

    @Transactional(readOnly = true)
    public Optional<UserEntity> getByEmail(String email)
    {
        return userRepository.findByEmail(email);
    }

    /**
     * This method is necessary to generate a random password for the OAuth2 user only
     * */
    private String generateRandomPassword()
    {
        StringBuilder stringBuilder = new StringBuilder();
        int passwordSize = new Random().nextInt(10, 20);
        Random random = new Random();

        for (int i = 0; i < passwordSize; i++)
            stringBuilder.append((char) random.nextInt(32, 127));

        return stringBuilder.toString();
    }
}