package com.example.userservice.service;

import com.example.userservice.client.OrderServiceClient;
import com.example.userservice.dto.UserDto;
import com.example.userservice.jpa.UserEntity;
import com.example.userservice.jpa.UserRepository;
import com.example.userservice.vo.ResponseOrder;
import com.example.userservice.vo.ResponseUser;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@RefreshScope
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RestTemplate restTemplate;
    private final Environment env;

    private final OrderServiceClient orderServiceClient;


    private final CircuitBreakerFactory circuitBreakerFactory;


    @Override
    public UserDto createUser(UserDto userDto) {
        userDto.setUserId(UUID.randomUUID().toString());

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserEntity userEntity = mapper.map(userDto,UserEntity.class);

        userEntity.setEncryptedPwd(passwordEncoder.encode(userDto.getPwd()));
        userRepository.save(userEntity);



        return mapper.map(userEntity, UserDto.class);
    }

    @Override
    public UserDto getUserById(String userId) {
        UserEntity userEntity = userRepository.findByUserId(userId);
        if(userEntity==null){
            throw  new EntityNotFoundException("user not found");

        }


        ModelMapper mapper = new ModelMapper();
        // 굳이 설정 없어도됨
     //   mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);


        UserDto userDto = mapper.map(userEntity, UserDto.class);


//        String orderUrl=String.format(env.getProperty("order_service.url"),userId);
//
//
//        ResponseEntity<List<ResponseOrder>> orderListResponse = restTemplate.exchange(orderUrl,
//                HttpMethod.GET, null, new ParameterizedTypeReference<List<ResponseOrder>>() {
//        });
//        List<ResponseOrder> orders = orderListResponse.getBody();


//        List<ResponseOrder> orders = null;
//        try{
//            orders = orderServiceClient.getOrders(userId);
//        }
//        catch (FeignException ex){
//            log.error(ex.getMessage()+"......"+env.getProperty("order_service.exception.order_is_empty"));
//        }


        //   초창기 세팅          List<ResponseOrder> orders=new ArrayList<>();


        // 에러 디코더
    //    String property = env.getProperty("order_service.exception.order");
     //   List<ResponseOrder> orders= orderServiceClient.getOrders(userId);

        //서킷브레이커

        log.info("before call orders micorserivce");
        CircuitBreaker circuitbreaker = circuitBreakerFactory.create("circuitbreaker");
        List<ResponseOrder> orders = circuitbreaker.run(() -> orderServiceClient.getOrders(userId), throwable -> new ArrayList<>());
        log.info("after call orders micorserivce");

        userDto.setOrders(orders);

        return userDto;
    }

    @Override
    public Iterable<UserEntity> getUserByAll() {
        return userRepository.findAll();
    }

    @Override
    public UserDto getUserDetailsByEmail(String userName) {
        UserEntity entity = userRepository.findByEmail(userName);
        if(entity==null){
            throw  new EntityNotFoundException("user not found");
        }
        ModelMapper mapper = new ModelMapper();
        return mapper.map(entity, UserDto.class);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(username);
        if(userEntity==null){
            throw  new EntityNotFoundException("user not found");
        }

        return new User(userEntity.getEmail(), userEntity.getEncryptedPwd(),
                true,true,true,true,new ArrayList<>());


    }
}
