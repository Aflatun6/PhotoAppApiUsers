package com.avelibeyli.photoapp.api.users.service;

import com.avelibeyli.photoapp.api.users.data.UserEntity;
import com.avelibeyli.photoapp.api.users.data.UserRepository;
import com.avelibeyli.photoapp.api.users.shared.UserDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDto createUser(UserDto userDetails) {

        userDetails.setUserId(UUID.randomUUID().toString());
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserEntity userEntity = mapper.map(userDetails, UserEntity.class);

        userEntity.setEncryptedPassword(passwordEncoder.encode(userDetails.getPassword()));
        UserEntity savedUser = userRepository.save(userEntity);

        return mapper.map(savedUser, UserDto.class);
    }
}
