package com.example.userservice.security;


import com.example.userservice.dto.UserDto;
import com.example.userservice.service.UserService;
import com.example.userservice.vo.RequestLogin;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

@Slf4j
@RequiredArgsConstructor
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final UserService userService;
    private final Environment env;


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
      //  String username = request.getParameter("username");  // ❌ JSON 데이터에서는 null이 반환됨
    //    String password = request.getParameter("password");
        // 이런식으로 하면 본문이 json 형식인 경우는 읽지못함  마치 @RequestParam 이 폼데이터는 읽지만 json 은 못읽는것처럼
        // 따라서 마치 @RequestBody 를 사용하듯이 getInputStream() 로 직접 본문을 읽을수있음


        try {
            RequestLogin cred= new ObjectMapper().readValue(request.getInputStream(), RequestLogin.class);

            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(cred.getEmail(), cred.getPassword(), new ArrayList<>());

            //getAuthenticationManager().authenticate(token)**는
            // 내부적으로 UserDetailsService를 호출하여 loadUserByUsername()을 실행하고, 사용자 정보를 기반으로
            // **UsernamePasswordAuthenticationToken**을 생성한 후 인증을 시도
            //userDetails 를 직접 사용하는경우는 위에 new UsernamePasswordAuthenticationToken 에 직접 넣어야함
            return getAuthenticationManager().authenticate(token);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }



    }


    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult)
            throws IOException, ServletException {


          String userName=  ( (User) authResult.getPrincipal()).getUsername();

          UserDto userDetails=userService.getUserDetailsByEmail(userName);

        String token = Jwts.builder()
                .setSubject(userDetails.getUserId())
                .setExpiration(new Date(System.currentTimeMillis() +
                        Long.parseLong(env.getProperty("token.expiration_time"))))
                .signWith(SignatureAlgorithm.HS512, env.getProperty("token.secret"))
                .compact();



        response.addHeader("token",token);
        response.addHeader("userId",userDetails.getUserId());


    }
}
