package com.example.userservice.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class Resilience4JConfig {




    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> globalCustomConfiguration(){

        CircuitBreakerConfig circuitBreakerFactory = CircuitBreakerConfig.custom()
                .failureRateThreshold(4)
                .waitDurationInOpenState(Duration.ofSeconds(1))
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .slidingWindowSize(2)
                .build();
        TimeLimiterConfig timeLimiterConfig=TimeLimiterConfig.custom()
                .timeoutDuration(Duration.ofSeconds(4))
                .build();







        return factory -> factory.configureDefault(id ->new Resilience4JConfigBuilder(id)
                .timeLimiterConfig(timeLimiterConfig)
                .circuitBreakerConfig(circuitBreakerFactory)
                .build()

        );


    }

        // 서킷브레이커의 컨피규얼 레이션 값과 타임 리미터의 컨피규얼 레이션 값

    }



