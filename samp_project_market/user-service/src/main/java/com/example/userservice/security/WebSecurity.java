package com.example.userservice.security;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@EnableWebSecurity
public class WebSecurity {
    private static final String[] WHITE_LIST = {
            "/**",
            "/users/**"
    };

    //    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(csrf -> csrf
//                        .ignoringRequestMatchers(PathRequest.toH2Console())
//                        .disable()
//                )
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers(PathRequest.toH2Console()).permitAll()
//                )
//                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));
//        return http.build();
//    }
    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable());
        http.headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.disable())
        );
//                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));
        http.authorizeHttpRequests(authorize -> authorize
//                   .requestMatchers(PathRequest.toH2Console()).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/**")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/users/**")).permitAll()
                        .requestMatchers(PathRequest.toH2Console()).permitAll()
        );
        return http.build();
    }

//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        return http
//                .csrf(csrf -> csrf.disable())
//                .headers(authorize -> authorize
//                        .frameOptions((frameOptions)-> frameOptions.disable()))
//                .authorizeHttpRequests(authorize -> authorize
//                        .requestMatchers(WHITE_LIST).permitAll()
//                        .requestMatchers(PathRequest.toH2Console()).permitAll())
//                .getOrBuild();
//    }
}
