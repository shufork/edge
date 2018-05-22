package me.shufork.auth.oauth;

import lombok.extern.slf4j.Slf4j;
import me.shufork.common.constants.RedisKeyConstants;
import me.shufork.common.dto.session.UserSessionDto;
import me.shufork.common.security.AuthDetails;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
public class TokenStoreImpl implements TokenStore {
    private final TokenStore backendTokenStore;
    private final RedisTemplate<String, Object> redisTemplate;

    public TokenStoreImpl(TokenStore backendTokenStore, RedisTemplate<String, Object> redisTemplate) {
        this.backendTokenStore = backendTokenStore;
        this.redisTemplate = redisTemplate;
    }

    private void updateSession(final OAuth2Authentication oAuth2Authentication){
        // Authentication
        Optional.ofNullable(oAuth2Authentication.getUserAuthentication()).ifPresent(userAuthentication->{
            Object principal = userAuthentication.getPrincipal();
            // Get AuthDetails (Created by RemoteUserDetailsService) ,Update UserSessionDto in redis
            if (principal instanceof AuthDetails) {
                AuthDetails authDetails = (AuthDetails) principal;

                UserSessionDto userSessionDto = new UserSessionDto();
                userSessionDto.setId(authDetails.getId());
                userSessionDto.setLoginName(authDetails.getUsername());
                userSessionDto.setDisplayName(authDetails.getDisplayName());

                log.trace("update redis for UserSessionDto :{},{}",userSessionDto.getLoginName(),userSessionDto.getId());
                BoundValueOperations<String, Object> boundValueOperations
                        = redisTemplate.boundValueOps(String.format("%s:%s", RedisKeyConstants.LOGIN_NAME_TO_USER_SESSION, userSessionDto.getLoginName()));
                boundValueOperations.setIfAbsent(userSessionDto);
                boundValueOperations.expire(2, TimeUnit.HOURS);
            }
        });
    }
    @Override
    public OAuth2Authentication readAuthentication(OAuth2AccessToken token) {
        OAuth2Authentication oAuth2Authentication = backendTokenStore.readAuthentication(token);
        updateSession(oAuth2Authentication);
        return oAuth2Authentication;
    }

    @Override
    public OAuth2Authentication readAuthentication(String token) {
        return backendTokenStore.readAuthentication(token);
    }

    @Override
    public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
        backendTokenStore.storeAccessToken(token, authentication);
    }

    @Override
    public OAuth2AccessToken readAccessToken(String tokenValue) {
        return backendTokenStore.readAccessToken(tokenValue);
    }

    @Override
    public void removeAccessToken(OAuth2AccessToken token) {
        backendTokenStore.removeAccessToken(token);
    }

    @Override
    public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {
        backendTokenStore.storeRefreshToken(refreshToken, authentication);
    }

    @Override
    public OAuth2RefreshToken readRefreshToken(String tokenValue) {
        return backendTokenStore.readRefreshToken(tokenValue);
    }

    @Override
    public OAuth2Authentication readAuthenticationForRefreshToken(OAuth2RefreshToken token) {
        return backendTokenStore.readAuthenticationForRefreshToken(token);
    }

    @Override
    public void removeRefreshToken(OAuth2RefreshToken token) {
        backendTokenStore.removeRefreshToken(token);
    }

    @Override
    public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken) {
        backendTokenStore.removeAccessTokenUsingRefreshToken(refreshToken);
    }

    @Override
    public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
        return backendTokenStore.getAccessToken(authentication);
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientIdAndUserName(String clientId, String userName) {
        return backendTokenStore.findTokensByClientIdAndUserName(clientId, userName);
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientId(String clientId) {
        return backendTokenStore.findTokensByClientId(clientId);
    }
}
