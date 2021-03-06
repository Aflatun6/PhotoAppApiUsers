package com.avelibeyli.photoapp.api.users.ui.controller;

import com.avelibeyli.photoapp.api.users.service.UserService;
import com.avelibeyli.photoapp.api.users.shared.UserDto;
import com.avelibeyli.photoapp.api.users.ui.model.UserResponseModel;
import com.avelibeyli.photoapp.api.users.ui.model.signUp.UserRequest;
import com.avelibeyli.photoapp.api.users.ui.model.signUp.UserResponse;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.ws.rs.Path;

@RestController
@RequestMapping("/users")
public class UsersController {

    private final Environment env;
    private final UserService userService;

    @Autowired
    public UsersController(Environment env, UserService userService) {
        this.env = env;
        this.userService = userService;
    }

    @GetMapping("/status/check")
    public String status() {
        return String.format("working on the port: %s, secret key: %s", env.getProperty("local.server.port"), env.getProperty("token.secret"));
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest user) {

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserDto userDto = mapper.map(user, UserDto.class);

        UserDto savedUser = userService.createUser(userDto);

        UserResponse userResponse = mapper.map(savedUser, UserResponse.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseModel> getUser(@PathVariable String id) {

        UserDto userDto = userService.getUserDetailsByUserIdWithFeign(id);
        UserResponseModel returnValue = new ModelMapper().map(userDto, UserResponseModel.class);

        return ResponseEntity.status(HttpStatus.OK).body(returnValue);
    }

}
