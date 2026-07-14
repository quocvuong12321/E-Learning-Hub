package com.king.lms.e_learning_hub.service;


import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.king.lms.e_learning_hub.dto.authenticate.AccessTokenResponse;
import com.king.lms.e_learning_hub.dto.authenticate.AuthenticateRequest;
import com.king.lms.e_learning_hub.dto.authenticate.AuthenticateResponse;
import com.king.lms.e_learning_hub.dto.authenticate.RefreshTokenRequest;
import com.king.lms.e_learning_hub.dto.oauth2.GoogleOAuthConfigResponse;
import com.king.lms.e_learning_hub.dto.oauth2.OAuthLoginRequest;
import com.king.lms.e_learning_hub.dto.user.UserRequest;
import com.king.lms.e_learning_hub.dto.user.UserResponse;
import com.king.lms.e_learning_hub.entity.Role;
import com.king.lms.e_learning_hub.entity.User;
import com.king.lms.e_learning_hub.enums.AuthProvider;
import com.king.lms.e_learning_hub.enums.TokenType;
import com.king.lms.e_learning_hub.exception.AppException;
import com.king.lms.e_learning_hub.exception.ErrorCode;
import com.king.lms.e_learning_hub.mapper.UserMapper;
import com.king.lms.e_learning_hub.repository.RoleRepository;
import com.king.lms.e_learning_hub.repository.UserRepository;
import com.king.lms.e_learning_hub.util.JwtUtils;
import com.king.lms.e_learning_hub.util.RedisUtils;
import com.nimbusds.jose.JOSEException;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class JwtAuthenticationService implements BaseAuthenticationService {
    JwtUtils jwtUtils;
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    RedisUtils redisUtils;
    UserMapper userMap;
    RoleRepository roleRepository;
    GoogleOAuthService googleOAuthService;

   /**
     * ✅ Local login (username/password)
     */
   @Override
   public AuthenticateResponse login(AuthenticateRequest request) {
       User user = userRepository.findByUsername(request.getUsername())
               .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));

       // Kiểm tra user là OAuth user (không có password)
       if (user.getPassword() == null) {
           throw new AppException(ErrorCode.USER_NOT_EXIST);
       }

       // Verify password
       if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
           throw new AppException(ErrorCode.PASSWORD_INCORRECT);
       }

       // Update last login
       user.setLastLoginAt(LocalDateTime.now());
       user.setActive(true);
       userRepository.save(user);


       return generateAuthResponse(user);
   }

   /**
    * ✅ Refresh access token
    */
   @Override
   public AccessTokenResponse RefreshToken(RefreshTokenRequest request) throws ParseException, JOSEException {
       String refreshToken = request.getRefreshToken();
       String username = jwtUtils.getUserName(refreshToken, TokenType.refresh);

       String hashToken = jwtUtils.hashToken(refreshToken);
       String storedToken = redisUtils.get(jwtUtils.getRefreshTokenKey(username), String.class);

       if (!hashToken.equals(storedToken)) {
           throw new AppException(ErrorCode.INVALID_TOKEN);
       }

       User user = userRepository.findByUsername(username)
               .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));

       // Generate new access token
       String newAccessToken = jwtUtils.generateToken(user, TIME_ACCESS, TokenType.access);


       return AccessTokenResponse.builder()
               .accessToken(newAccessToken)
               .build();
   }

   /**
    * ✅ Logout
    */
   @Override
   public void logout() throws ParseException, JOSEException {
       String username = jwtUtils.getUserNameByAuthentication();
       jwtUtils.deleteRefreshToken(username);
       SecurityContextHolder.clearContext();
   }

   /**
    * ✅ Register local account (username/password)
    */
   @Transactional
   public UserResponse register(UserRequest request) {
       // Validate username
       if (userRepository.existsByUsername(request.getUsername())) {
           throw new AppException(ErrorCode.USERNAME_EXISTED);
       }

       // Validate email
       if (userRepository.existsByEmail(request.getEmail())) {
           throw new AppException(ErrorCode.EMAIL_EXISTED);
       }

       // Validate password match
       if (!request.getPassword().equals(request.getConfirmPassword())) {
           throw new AppException(ErrorCode.PASSWORD_NOT_MATCH);
       }

       // Encode password
       String hashPass = passwordEncoder.encode(request.getPassword());

       // Create user
       User user = userMap.toUser(request);
       user.setPublicId(UUID.randomUUID().toString());
       user.setPassword(hashPass);
       user.setAuthProvider(AuthProvider.LOCAL);
       user.setActive(true);

       // Assign customer role
       Role customerRole = roleRepository.findByName("customer")
               .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXIST));
       user.setRoles(Collections.singleton(customerRole));

       User savedUser = userRepository.save(user);

       return userMap.toResponse(savedUser);
   }


    /**
     * ✅ Get google url login cofig
     * delegate to GoogleOAuthService
     */
    public GoogleOAuthConfigResponse getGoogleOAuthConfig() {
       return googleOAuthService.getGoogleOAuthConfig();
    }

   /**
    * ✅ OAuth2 login (Google, Facebook, etc)
    * Receives authorization code từ frontend và delegate to GoogleOAuthService
    */
   @Transactional
   public AuthenticateResponse loginWithOAuth(OAuthLoginRequest request) {
       try {
           // Delegate to GoogleOAuthService to handle OAuth flow
           return googleOAuthService.handleOAuth(request);
       } catch (Exception e) {
           throw new AppException(ErrorCode.OAUTH2_ERROR);
       }
   }

   

   /**
    * ✅ OAuth login response
    */
   public AuthenticateResponse generateOAuthTokens(User user, String provider) {
    // Update last login
    user.setLastLoginAt(LocalDateTime.now());
    user.setEnabled(true);
    userRepository.save(user);

    // Generate access token (20 phút)
    String accessToken = jwtUtils.generateToken(user, TIME_ACCESS, TokenType.access);

    // Generate refresh token (20 ngày)
    String refreshToken = jwtUtils.generateToken(user, TIME_REFRESH, TokenType.refresh);

    // Store refresh token in Redis
    jwtUtils.storeRefreshToken(user.getUsername(), refreshToken, (int)TIME_REFRESH);


    return AuthenticateResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .user(userMap.toResponse(user))  // ← OAuth: có user info
            .provider(provider)  // ← OAuth: có provider
            .tokenType("Bearer")
            .expiresIn((int)(TIME_ACCESS * 60))  // Convert to seconds
            .build();
}

    /**
    * ✅ Local login response
    */
    private AuthenticateResponse generateAuthResponse(User user) {
        String accessToken = jwtUtils.generateToken(user, TIME_ACCESS, TokenType.access);
        String refreshToken = jwtUtils.generateToken(user, TIME_REFRESH, TokenType.refresh);
 
        // Store refresh token in Redis
        jwtUtils.storeRefreshToken(user.getUsername(), refreshToken, (int)TIME_REFRESH);
 
        String roles = jwtUtils.getStringRole(user.getRoles());
 
        return AuthenticateResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .role(roles)  // ← Local login: có role
                .mustChangePassword(false)
                .tokenType("Bearer")
                .expiresIn((int)(TIME_ACCESS * 60))  // Convert to seconds
                .build();
    }
 
    
}
