package com.avelibeyli.photoapp.api.users.service;

import com.avelibeyli.photoapp.api.users.shared.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    UserDto createUser(UserDto userDetails);
    UserDto getUserDetailsByEmail(String email);
}
