package com.king.lms.e_learning_hub.service;


import com.king.lms.e_learning_hub.dto.authenticate.AuthenticateResponse;
import com.king.lms.e_learning_hub.dto.user.ChangePasswordRequest;
import com.king.lms.e_learning_hub.dto.user.UserRequest;
import com.king.lms.e_learning_hub.dto.user.UserResponse;
import com.king.lms.e_learning_hub.dto.user.UserUpdateRequest;
import com.king.lms.e_learning_hub.entity.Role;
import com.king.lms.e_learning_hub.entity.User;
import com.king.lms.e_learning_hub.enums.TokenType;
import com.king.lms.e_learning_hub.exception.AppException;
import com.king.lms.e_learning_hub.exception.ErrorCode;
import com.king.lms.e_learning_hub.mapper.UserMapper;
import com.king.lms.e_learning_hub.repository.UserRepository;
import com.king.lms.e_learning_hub.util.JwtUtils;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class AccountService {
    PasswordEncoder encoder;
     UserRepository userRepository;
    UserMapper userMap;
    JwtUtils jwtUtils;
    public UserResponse updateAccount(UserUpdateRequest request){

        String username  = jwtUtils.getUserNameByAuthentication();

        User u = userRepository.findByUsername(username).orElseThrow(()->new AppException(ErrorCode.USER_NOT_EXIST));

        u.setFullName(request.getFullName());
        u.setEmail(request.getEmail());

        return userMap.toResponse(userRepository.save(u));
    }

    public AuthenticateResponse changePassword(ChangePasswordRequest request){

        if(!request.getPassword().equals(request.getConfirmPassword()))
            throw new AppException(ErrorCode.PASSWORD_NOT_MATCH);

        String username = jwtUtils.getUserNameByAuthentication();

        User u = userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));

        String hashPassword = encoder.encode(request.getPassword());

        u.setPassword(hashPassword);

        jwtUtils.deleteRefreshToken(username);

        String newAccessToken = jwtUtils.generateToken(u,BaseAuthenticationService.TIME_ACCESS, TokenType.access);
        String newRefreshToken = jwtUtils.generateToken(u,BaseAuthenticationService.TIME_REFRESH,TokenType.refresh);

        jwtUtils.storeRefreshToken(u.getUsername(),newRefreshToken,(int)BaseAuthenticationService.TIME_REFRESH);

        userRepository.save(u);


        return AuthenticateResponse.builder()
                .refreshToken(newRefreshToken)
                .accessToken(newAccessToken)
                .role(jwtUtils.getStringRole(u.getRoles()))
                .build();



    }
}
