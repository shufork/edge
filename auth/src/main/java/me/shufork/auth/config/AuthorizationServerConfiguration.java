package me.shufork.auth.config;

import me.shufork.auth.oauth.TokenStoreImpl;
import me.shufork.common.rpc.client.user.UserClient;
import me.shufork.common.service.RemoteUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.JdbcApprovalStore;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

import javax.sql.DataSource;

@Configuration
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private DataSource dataSource;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    @Qualifier("commonRedisTemplate")
    private RedisTemplate redisTemplate;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    @Qualifier("remoteUserDetailsService")
    private UserDetailsService userDetailsService;

    @Autowired
    @Qualifier("customClientDetailsService")
    private ClientDetailsService clientDetailsService;

    @Autowired
    private AuthorizationCodeServices authorizationCodeServices;

    @Autowired
    private ApprovalStore approvalStore;

    @Autowired
    private TokenStore tokenStore;

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {

/*        clients.inMemory()
                .withClient("acme")
                .secret("secret")
                .authorizedGrantTypes("authorization_code", "refresh_token", "password", "client_credentials")
                .scopes("server")
                .autoApprove(true)
                .and()
                .withClient("acme2")
                .secret("secret")
                .authorizedGrantTypes("client_credentials", "refresh_token")
                .scopes("server")
                .accessTokenValiditySeconds(300)
                .refreshTokenValiditySeconds(300);*/

        clients.withClientDetails(clientDetailsService);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
                .authenticationManager(authenticationManager)
                .userDetailsService(userDetailsService)
                .authorizationCodeServices(authorizationCodeServices)
                .approvalStore(approvalStore)
                .tokenStore(tokenStore);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security
                .tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()");
    }

    @Bean
    public TokenStore tokenStore(RedisConnectionFactory connectionFactory) {
        //return new InMemoryTokenStore();
        return new TokenStoreImpl(new JdbcTokenStore(dataSource), redisTemplate);
    }

    @Bean
    public ApprovalStore approvalStore() {
        return new JdbcApprovalStore(dataSource);
    }

    @Bean
    public AuthorizationCodeServices authorizationCodeServices() {
        return new JdbcAuthorizationCodeServices(dataSource);
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean(name = "customClientDetailsService")
    public ClientDetailsService clientDetailsService() {
        return new JdbcClientDetailsService(dataSource);
    }

    @Bean(name = "remoteUserDetailsService")
    public RemoteUserDetailsService remoteUserDetailsService(@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") UserClient userClient) {
        return new RemoteUserDetailsService(userClient);
    }

}
