package com.example.userservice.security;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.IpAddressMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import java.util.function.Supplier;

@Configuration
@EnableWebSecurity
public class WebSecurity {
    private static final String IP_ADDRESS = "59.10.164.35";
    private static final String SUBNET = "/32";
    private static final IpAddressMatcher IP_ADDRESS_MATCHER = new IpAddressMatcher(IP_ADDRESS + SUBNET);
    private static final String[] WHITE_LIST = {
            "/**",
            "/users/**"
    };

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.disable())
                )
                .authorizeHttpRequests(authorize -> authorize
//                        .requestMatchers(AntPathRequestMatcher.antMatcher("/users/**")).permitAll()
                                .requestMatchers(AntPathRequestMatcher.antMatcher("/**")).access(this::hasIpAddress)
                                .requestMatchers(PathRequest.toH2Console()).permitAll()

                );
        return http.build();
    }

    private AuthorizationDecision hasIpAddress(Supplier<Authentication> authentication, RequestAuthorizationContext object) {
        return new AuthorizationDecision(IP_ADDRESS_MATCHER.matches(object.getRequest()));
    }

}
