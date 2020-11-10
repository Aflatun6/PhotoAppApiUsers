package com.avelibeyli.photoapp.api.users.service;

import com.avelibeyli.photoapp.api.users.data.UserEntity;
import com.avelibeyli.photoapp.api.users.data.UserRepository;
import com.avelibeyli.photoapp.api.users.shared.UserDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.UUID;

@Service
public class    UserServiceImpl implements UserService {

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

    @Override
    public UserDto getUserDetailsByEmail(String email) { // this method is just to get userDto and then userId, we will add it as a subject to Jws Token
        UserEntity userEntity = userRepository.findByEmail(email);
        if (userEntity == null) throw new UsernameNotFoundException(email);
        return new ModelMapper().map(userEntity, UserDto.class);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException { // This function will be called automatically by spring security when we login
        UserEntity userEntity = userRepository.findByEmail(email); //  ATTEMPTAUTH METHOD WILL CALL THIS METHOD. AND IF USER WILL BE FOUND AND USER.CLASS WILL BE AUTHED SO SUCCESSIVE METHOD WILL BE TRIGGERED
        if (userEntity == null) throw new UsernameNotFoundException(email);
        return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), true, true, true, true, new ArrayList<>()); // here we return authenticated User class instance.
    } // so a User will be created and returned to spring security with our logged in user's credentials;
}
