package com.example.apigatewayservice.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class FilterConfig {

  //  @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
//        RouteLocatorBuilder의 routes() 메서드에서 Builder 객체를 얻고, 그 Builder 객체에서
//        route() 메서드를 호출하면서 라우트를 정의하는 방식입니다.
//                이때 r은 RouteSpec 객체이며, path() 메서드 호출로 경로 조건을 설정
        return builder.routes()
                .route(r ->  r.path("/first-service/**")
                        .filters(f->f.addRequestHeader("first-request","first-request-header")
                        .addResponseHeader("first-response","first-response-header")
                        )
                        .uri("http://localhost:8081"))


                .route(r ->  r.path("/second-service/**")
                        .filters(f->f.addRequestHeader("second-request","second-request-header")
                                .addResponseHeader("second-response","second-response-header")
                        )
                        .uri("http://localhost:8082"))

                .build();


    }

}
