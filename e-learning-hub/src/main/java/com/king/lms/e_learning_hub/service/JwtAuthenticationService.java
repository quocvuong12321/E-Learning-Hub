package com.king.lms.e_learning_hub.service;


import com.king.lms.e_learning_hub.dto.Response.ApiResponse;
import com.king.lms.e_learning_hub.dto.authenticate.AccessTokenResponse;
import com.king.lms.e_learning_hub.dto.authenticate.AuthenticateRequest;
import com.king.lms.e_learning_hub.dto.authenticate.AuthenticateResponse;
import com.king.lms.e_learning_hub.dto.authenticate.RefreshTokenRequest;
import com.king.lms.e_learning_hub.entity.Role;
import com.king.lms.e_learning_hub.entity.User;
import com.king.lms.e_learning_hub.enums.TokenType;
import com.king.lms.e_learning_hub.exception.AppException;
import com.king.lms.e_learning_hub.exception.ErrorCode;
import com.king.lms.e_learning_hub.repository.UserRepository;
import com.king.lms.e_learning_hub.util.JwtUtils;
import com.king.lms.e_learning_hub.util.RedisUtils;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.print.DocFlavor;
import java.text.ParseException;
import java.util.HashSet;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class JwtAuthenticationService implements BaseAuthenticationService {
    JwtUtils jwtUtils;
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    RedisUtils redisUtils;
    @Override
    public AuthenticateResponse login(AuthenticateRequest request) {

        User user = userRepository.findByUsername(request.getUsername()).orElseThrow(()->new AppException(ErrorCode.USER_NOT_EXIST));
        if(!passwordEncoder.matches(request.getPassword(),user.getPassword()))
            throw new AppException(ErrorCode.PASSWORD_INCORRECT);

        String accessToken = jwtUtils.generateToken(user,TIME_ACCESS, TokenType.access);
        String refreshToken = jwtUtils.generateToken(user,TIME_REFRESH,TokenType.refresh);

        jwtUtils.storeRefreshToken(user.getUsername(),refreshToken,(int)TIME_REFRESH);

        String roles = String.join(" ",user.getRoles().stream().map(Role::getName).toList());

        return AuthenticateResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .mustChangePassword(false)
                .role(roles)
                .build();
    }

    @Override
    public AccessTokenResponse RefreshToken(RefreshTokenRequest request) throws ParseException, JOSEException {

        String username = jwtUtils.getUserName(request.getRefreshToken(),TokenType.refresh);

        String hashToken = jwtUtils.hashToken(request.getRefreshToken());

        String storedToken = redisUtils.get(jwtUtils.getRefreshTokenKey(username), String.class);

        if(!hashToken.equals(storedToken))
            throw new AppException(ErrorCode.INVALID_TOKEN);

        User u = userRepository.findByUsername(username).orElseThrow(()-> new AppException(ErrorCode.USER_NOT_EXIST));

        String newAccessToken = jwtUtils.generateToken(u,TIME_ACCESS,TokenType.access);

        return AccessTokenResponse.builder()
                .accessToken(newAccessToken)
                .build();
    }

    @Override
    public void logout() throws ParseException, JOSEException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication==null){
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        jwtUtils.deleteRefreshToken(authentication.getName());
        SecurityContextHolder.clearContext();


    }
}
