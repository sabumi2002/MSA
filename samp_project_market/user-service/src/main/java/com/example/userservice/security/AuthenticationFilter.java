package com.example.userservice.security;

import com.example.userservice.dto.UserDto;
import com.example.userservice.service.UserService;
import com.example.userservice.vo.RequestLogin;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.security.Key;
import java.util.ArrayList;
import java.util.Date;

@Slf4j
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
//    private final AuthenticationManager authenticationManager;
//
//    public AuthenticationFilter(AuthenticationManager authenticationManager){
//        this.authenticationManager = authenticationManager;
//    }
    private UserService userService;
    // 사용자가 만들었던 토큰에대한 만료기간, 토큰을 만들기위한 알고리즘만들때 자바코드안에 토큰정보를 넣는것이아니라 application.yml파일에서 관리할것이기 때문에 필요하다.
    private Environment env;
    private final Key key;
    public AuthenticationFilter(AuthenticationManager authenticationManager,
                                UserService userService,
                                Environment env) {
        super.setAuthenticationManager(authenticationManager);  // super(authenticationManager)와 같은 방법
        this.userService = userService;
        this.env = env;
        byte[] keyBytes = Decoders.BASE64.decode(env.getProperty("token.secret"));
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

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
        // 로그인성공하면 userName을 가져오자
        String userName = ((User)authResult.getPrincipal()).getUsername();
        // userName으로 user정보를 가져오자
        UserDto userDetails = userService.getUserDetailsByEmail(userName);


        // token 생성
        String token = Jwts.builder()
                .setSubject(userDetails.getUserId())
                .setExpiration(new Date(System.currentTimeMillis() +
                        Long.parseLong(env.getProperty("token.expiration_time")))) // 토큰 유효시간 설정
//                .signWith(SignatureAlgorithm.HS512, env.getProperty("token.secret"))    // 암호화
                .signWith(key, SignatureAlgorithm.HS512)    // 암호화
                .compact();

        // token정보 주입
        response.addHeader("token", token);
        // 정상적으로 만들어진 token인지 확인하는 작업을 위해 userId 주입
        response.addHeader("userId", userDetails.getUserId());

    }
}
