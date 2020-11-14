package com.avelibeyli.photoapp.api.users.service;

import com.avelibeyli.photoapp.api.users.albums.AlbumsServiceClient;
import com.avelibeyli.photoapp.api.users.data.UserEntity;
import com.avelibeyli.photoapp.api.users.data.UserRepository;
import com.avelibeyli.photoapp.api.users.shared.UserDto;
import com.avelibeyli.photoapp.api.users.albums.AlbumResponseModel;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RestTemplate restTemplate;
    private final Environment environment;
    private final AlbumsServiceClient albumsServiceClient;
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, RestTemplate restTemplate, Environment environment, AlbumsServiceClient albumsServiceClient) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.restTemplate = restTemplate;
        this.environment = environment;
        this.albumsServiceClient = albumsServiceClient;
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

    @Override
    public UserDto getUserDetailsByUserIdWithRestTemplate(String id) {
        UserEntity userEntity = userRepository.findByUserId(id);
        if (userEntity == null) throw new UsernameNotFoundException(String.format("User with id: %s not found", id));
        UserDto userDto = new ModelMapper().map(userEntity, UserDto.class);

        String albumsUrl = String.format(environment.getProperty("albums.url"), id);

        ResponseEntity<List<AlbumResponseModel>> exchange = restTemplate.exchange(albumsUrl, HttpMethod.GET, null, new ParameterizedTypeReference<List<AlbumResponseModel>>() {
        });

        List<AlbumResponseModel> albums = exchange.getBody();
        userDto.setAlbums(albums);
        return userDto;
    }

    @Override
    public UserDto getUserDetailsByUserIdWithFeign(String id) {
        UserEntity userEntity = userRepository.findByUserId(id);
        if (userEntity == null) throw new UsernameNotFoundException(String.format("User with id: %s not found", id));
        UserDto userDto = new ModelMapper().map(userEntity, UserDto.class);

        List<AlbumResponseModel> albums = null;

        albums = albumsServiceClient.getAlbums(id);

//        try {
//            albums = albumsServiceClient.getAlbums(id);
//        } catch (FeignException e) {
//            logger.error(e.getLocalizedMessage());
//        }

        userDto.setAlbums(albums);
        return userDto;
    }
}
