package com.example.userservice;

//import com.example.userservice.error.FeignErrorDecoder;
import feign.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.context.scope.refresh.RefreshScope;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


    //이해했습니다! 이제 질문이 왜 http://ORDER-SERVICE/order-service/{userId}/orders처럼 API Gateway를 거치지 않고
    // order-service라는 서비스 이름을 바로 호출할 수 있는지에 대해 궁금한 거죠?
    //
    //이것에 대해 설명드리겠습니다.
    //
    //1. @LoadBalanced RestTemplate을 사용한 경우
    //RestTemplate에 @LoadBalanced 어노테이션을 붙이면, 이 RestTemplate은 Eureka와 통합되어,
    // 서비스 이름을 Eureka에서 등록된 실제 서비스 인스턴스로 변환합니다.
    // 즉, @LoadBalanced가 적용된 RestTemplate은 Eureka에서 order-service의 실제 주소를 찾아서 요청을 보내게 됩니다.
    //
    //이때 http://ORDER-SERVICE/order-service/{userId}/orders처럼 서비스 이름을 직접 호출할 수 있는 이유는,
    // ORDER-SERVICE라는 서비스 이름이 Eureka에 등록되어 있기 때문입니다.
    // RestTemplate은 서비스 이름을 Eureka에서 찾고 실제 IP와 포트를 알아내어 해당 서비스에 요청을 전달하는 방식입니다.
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate(){
        return new RestTemplate();

    }

    @Bean
    public Logger.Level feignLoggerLevel(){
        return Logger.Level.FULL;
    }

//    @Bean
//    public FeignErrorDecoder getFeignErrorDecoder(Environment env){
//
//        return new FeignErrorDecoder(env);
//    }



}
