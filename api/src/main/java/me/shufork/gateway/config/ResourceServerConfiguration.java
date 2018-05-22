package me.shufork.gateway.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

@Configuration
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {
    @Override
    public void configure(HttpSecurity http) throws Exception {

        http
                .authorizeRequests()
                .antMatchers("/users/**").hasAuthority("ROLE_ADMIN")
                .antMatchers("/coc/**").hasAnyAuthority("ROLE_ADMIN","ROLE_USER")
                .anyRequest().authenticated();
    }
}
