package com.avelibeyli.photoapp.api.users.security;

import com.avelibeyli.photoapp.api.users.service.UserService;
import com.avelibeyli.photoapp.api.users.shared.UserDto;
import com.avelibeyli.photoapp.api.users.ui.model.signIn.UserRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;

import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.SecretKey;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final UserService userService;
    private final Environment env;
    private final SecretKey secretKey;
    private final JwtConfig jwtConfig;

    public AuthenticationFilter(UserService userService, Environment env, AuthenticationManager authenticationManager, SecretKey secretKey, JwtConfig jwtConfig) {
        this.userService = userService;
        this.env = env;
        this.secretKey = secretKey;
        this.jwtConfig = jwtConfig;
        super.setAuthenticationManager(authenticationManager);
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            UserRequest userRequest = new ObjectMapper().readValue(request.getInputStream(), UserRequest.class);

            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            userRequest.getEmail(),
                            userRequest.getPassword(),
                            new ArrayList<>()));

        } catch (IOException e) {
            throw new RuntimeException("bad");
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        String username = ((User) authResult.getPrincipal()).getUsername(); // I think this the User.class instance is the principal
        UserDto userDetails = userService.getUserDetailsByEmail(username);


//        here we literally create a token and add it to the Header.
        String token = Jwts.builder()
                .setSubject(userDetails.getUserId())
                .setIssuedAt(new Date())
                .setExpiration(java.sql.Date.valueOf(LocalDate.now().plusDays(jwtConfig.getTokenExpirationAfterDays())))
//                .signWith(SignatureAlgorithm.HS512, env.getProperty("token.secret"))
                
                .signWith(secretKey)
                .compact();

        response.addHeader(jwtConfig.getAuthorizationHeader(), jwtConfig.getTokenPrefix() + token);
        response.addHeader("userId", userDetails.getUserId());

    }
}
