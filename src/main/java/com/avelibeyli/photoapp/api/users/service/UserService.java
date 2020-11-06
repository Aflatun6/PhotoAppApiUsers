package com.avelibeyli.photoapp.api.users.service;

import com.avelibeyli.photoapp.api.users.shared.UserDto;

public interface UserService {
    UserDto createUser(UserDto userDetails);
}
