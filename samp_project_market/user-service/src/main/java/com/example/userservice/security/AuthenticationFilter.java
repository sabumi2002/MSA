package com.example.userservice.security;

import com.example.userservice.vo.RequestLogin;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.ArrayList;

@Slf4j
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
//    private final AuthenticationManager authenticationManager;
//
//    public AuthenticationFilter(AuthenticationManager authenticationManager){
//        this.authenticationManager = authenticationManager;
//    }


    @Override
    // 요청정보를 보냈을때 그것을 처리해줄수있는 메소드 재정의
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        try {
            // request.getInputStream() : 전달시켜주려는 로그인값이 post형식으로 전달된다. post형식을 requestParameter로 받을수 없기때문에 inputstream으로 수작업하도록한다.
            RequestLogin creds = new ObjectMapper().readValue(request.getInputStream(), RequestLogin.class);

            // getAuthenticationManager().authenticate(): 인증작업 요청
            // 사용자가 입력했던 email(ID), password 값을 스프링시큐리티에서 사용할수있는 형태의값으로 변환하기위해선 UsernamePasswordAuthenticationToken 으로 변경할 필요가 있다.
            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            creds.getEmail(),       // ID
                            creds.getPassword(),    // PW
                            new ArrayList<>()       // 어떤 권한을 가질것인지
                    )
            );
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    // 실제 로그인 성공했을때 정확히 어떤 처리를 해줄것인지 정의
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        log.debug(((User)authResult.getPrincipal()).getUsername());
    }
}
