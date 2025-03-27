package com.example.userservice.security;

import com.example.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.ws.rs.HttpMethod;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurity extends WebSecurityConfigurerAdapter {
    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final Environment env;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
       // http.authorizeRequests().antMatchers(HttpMethod.POST  ,"/users/**").permitAll();

        http.authorizeRequests().antMatchers("/actuator/**").permitAll();
        http.authorizeRequests().antMatchers("/**")
              //  .hasIpAddress("192.168.75.157")
                .hasIpAddress("172.20.0.5")
                        .and()
                                .addFilter(getAuthenticationFilter());

        http.headers().frameOptions().disable();

    }

    private AuthenticationFilter getAuthenticationFilter() throws Exception {

        AuthenticationFilter filter = new AuthenticationFilter(userService,env);
        //authenticationManager() 메서드가 별도로 구현되지 않았는데도 바로 사용 가능한 이유는,
        // WebSecurityConfigurerAdapter 클래스를 상속한 경우
        // Spring Security가 기본적으로 제공하는 authenticationManager() 메서드를 자동으로 사용할 수 있기 때문입니다.

        filter.setAuthenticationManager(authenticationManager() );
        return filter;


    }
    // select pwd from users where email=?
    // db_pwd(encrypted) ==input_pws(encrypted)
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);
    }











}
