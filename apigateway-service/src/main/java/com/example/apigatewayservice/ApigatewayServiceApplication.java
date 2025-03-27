package com.example.apigatewayservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.boot.actuate.trace.http.InMemoryHttpTraceRepository;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ApigatewayServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApigatewayServiceApplication.class, args);
    }
    @Bean
    public HttpTraceRepository httpTraceRepository() {
        //클라이언트 요청이 메모리에 담겨 필요할때 엔드포인트로 정보를 얻으 수 있다
        return new InMemoryHttpTraceRepository();
    }

}
