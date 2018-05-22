package me.shufork.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;

@SpringBootApplication(scanBasePackages = {"me.shufork.auth", "me.shufork.common.rpc.client","me.shufork.common.config.redis"})
@EnableDiscoveryClient
@EnableFeignClients(basePackages = ("me.shufork.common.rpc.client"))
@EnableAuthorizationServer
//@EnableSwagger2
public class AuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }

}