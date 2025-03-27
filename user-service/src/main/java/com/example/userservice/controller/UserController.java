package com.example.userservice.controller;

import com.example.userservice.dto.UserDto;

import com.example.userservice.jpa.UserEntity;
import com.example.userservice.service.UserService;
import com.example.userservice.vo.Greeting;
import com.example.userservice.vo.RequestUser;
import com.example.userservice.vo.ResponseUser;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
//@RequestMapping("/user-service")
@RequestMapping("/")
@RequiredArgsConstructor
public class UserController {

    private final  Environment env;
    private final Greeting greeting;
    private final UserService userService;

    @Timed(value="users.status",longTask = true)
    @GetMapping("/health_check")
    public String status(){
        return String.format("It's working in user service"
            +" , port(local.server.port)="   + env.getProperty("local.server.port")
                +" , port(server.port)="   + env.getProperty("server.port")
                +" , token secret="   + env.getProperty("token.secret")
                +" , token expiration time="   + env.getProperty("token.expiration_time")




        );

    }

    @Timed(value="users.welcome",longTask = true)
    @GetMapping("/welcome")
    public String welcome(){
        return greeting.getMessage();

      //  return env.getProperty("greeting.message");


    }
    @PostMapping("/users")
    public  ResponseEntity<ResponseUser> createUser(@RequestBody RequestUser user){
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);


        UserDto userDto = mapper.map(user, UserDto.class);

        userService.createUser(userDto);

        ResponseUser responseUser = mapper.map(userDto, ResponseUser.class);

        return  ResponseEntity.status(HttpStatus.CREATED).body(responseUser);
    }

    @GetMapping("/users")
    public  ResponseEntity<List<ResponseUser>> getUsers(){

        Iterable<UserEntity> users = userService.getUserByAll();
        ModelMapper mapper = new ModelMapper();

        List<ResponseUser> result = new ArrayList<>();


        for (UserEntity user : users) {
            ResponseUser ResponseUser = mapper.map(user, ResponseUser.class);
            result.add(ResponseUser);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);

    }

    @GetMapping("users/{userId}")
    public  ResponseEntity<ResponseUser> getUser(@PathVariable String userId){

        UserDto userDto = userService.getUserById(userId);
        ModelMapper mapper = new ModelMapper();
        ResponseUser result = mapper.map(userDto, ResponseUser.class);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


}
