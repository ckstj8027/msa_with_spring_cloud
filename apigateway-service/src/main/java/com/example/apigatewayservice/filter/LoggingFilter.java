package com.example.apigatewayservice.filter;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class LoggingFilter extends AbstractGatewayFilterFactory<LoggingFilter.Config> {
    public LoggingFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        //custom pre filter
//        return (exchange, chain) ->
//        {
//
//
//            ServerHttpRequest request = exchange.getRequest();
//            ServerHttpResponse response = exchange.getResponse();
//
//            log.info("Global filter base message : {}",config.getBaseMessage());
//
//            if(config.isPreLogger()){
//                log.info("Global filter start : request id -> {}",request.getId());
//            }
//
//            // 커스텀 post 필터
//
//            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
//
//                if(config.isPostLogger()){
//                    log.info("Global filter end : response id  -> {}",response.getStatusCode());
//                }
//
//            }));
//
//
//        };

        GatewayFilter filter = new OrderedGatewayFilter((exchange,chain)->{
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            log.info("Logging filter base message : {}",config.getBaseMessage());

            if(config.isPreLogger()){
                log.info("Logging  pre filter  : request id -> {}",request.getId());
            }

            // 커스텀 post 필터

            return chain.filter(exchange).then(Mono.fromRunnable(() -> {

                if(config.isPostLogger()){
                    log.info("Logging  post filter  : response id -> {}",response.getStatusCode());
                }

            }));



        }, Ordered.LOWEST_PRECEDENCE);

        return filter;
    }

    @Data
    public static class Config {
        private  String baseMessage;
        private boolean preLogger;
        private boolean postLogger;
    }
}
