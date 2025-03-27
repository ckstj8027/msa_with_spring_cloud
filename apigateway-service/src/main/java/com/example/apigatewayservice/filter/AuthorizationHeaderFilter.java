package com.example.apigatewayservice.filter;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j

public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {

        private final Environment env;

    public AuthorizationHeaderFilter(Environment env) {
        super(Config.class);  // 부모 클래스의 생성자 호출
        this.env = env;  // 환경 변수를 생성자 주입
    }



    @Override
    public GatewayFilter apply(Config config) {
        return (

                (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            if(!request.getHeaders().containsKey("Authorization")) {
                return onError(exchange,"no authorization header", HttpStatus.UNAUTHORIZED);
            }

            String authorizationHeader=request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            String jwt=authorizationHeader.replace("Bearer ", "");

            if(!isJwtValid(jwt)) {

                return onError(exchange,"jw token is not valid", HttpStatus.UNAUTHORIZED);
            }



            return chain.filter(exchange);
            
    });


    }

    private boolean isJwtValid(String jwt) {
        boolean returnValue=true;

        String subject=null;

        try{
            subject= Jwts.parser().setSigningKey(env.getProperty("token.secret"))
                    .parseClaimsJws(jwt)
                    .getBody()
                    .getSubject();


        }
        catch(Exception e){
            returnValue=false;
        }

        if(subject==null || subject.isEmpty()){
            returnValue=false;

        }



        return returnValue;


    }



    // mono , flux -> spring webflux  클라이언트 요청이 들어왔을때 반환 타입 각각 단일 값과 다중 값 처리
    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();

        response.setStatusCode(httpStatus);

        log.error(err);
        return response.setComplete();
    }


    public static class Config{


        }

}
