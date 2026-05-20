package com.king.lms.e_learning_hub.service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.gson.Gson;
import com.king.lms.e_learning_hub.configuration.OAuth2Properties;
import com.king.lms.e_learning_hub.dto.authenticate.AuthenticateResponse;
import com.king.lms.e_learning_hub.dto.oauth2.GoogleOAuthConfigResponse;
import com.king.lms.e_learning_hub.dto.oauth2.GoogleTokenResponse;
import com.king.lms.e_learning_hub.dto.oauth2.GoogleUserInfo;
import com.king.lms.e_learning_hub.dto.oauth2.OAuthLoginRequest;
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

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
@AllArgsConstructor
@Slf4j
public class GoogleOAuthService implements OAuthAuthenticationService {

    private final OAuth2Properties oauth2Properties;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtUtils jwtUtils;
    private final UserMapper userMap;
    private final Gson gson = new Gson();

 /**
     * ✅ Get Google OAuth configuration
     * Build full authorization URL for frontend to redirect to Google
     * 
     * @return GoogleOAuthConfigResponse with authorizationUrl
     */
    public GoogleOAuthConfigResponse getGoogleOAuthConfig() {
        try {
            // ✅ Build authorization URL
            String authorizationUrl = String.format(
                "%s?client_id=%s&redirect_uri=%s&response_type=code&scope=%s&access_type=offline",
                oauth2Properties.getAuthorizationUrl(),
                URLEncoder.encode(oauth2Properties.getClientId(), StandardCharsets.UTF_8),
                URLEncoder.encode(oauth2Properties.getRedirectUri(), StandardCharsets.UTF_8),
                URLEncoder.encode("openid email profile", StandardCharsets.UTF_8)
            );

            log.info("✅ Google OAuth config generated");
            log.debug("📤 Authorization URL: {}", authorizationUrl);

            return GoogleOAuthConfigResponse.builder()
                    .authorizationUrl(authorizationUrl)
                    .build();

        } catch (Exception e) {
            log.error("❌ Error building Google OAuth config: {}", e.getMessage());
            throw new AppException(ErrorCode.OAUTH2_ERROR);
        }
    }
    
    /**
     * ✅ Handle Google OAuth flow
     */
    @Override
    @Transactional
    public AuthenticateResponse handleOAuth(OAuthLoginRequest request) {
        try {
            // Bước 1: Trao đổi code lấy token
            GoogleTokenResponse tokenResponse = exchangeCodeForToken(request.getCode());

            log.info("token response: {}",tokenResponse);
            // log.info("✅ User {} logged in via OAuth2 provider: {}", username, provider);

            if (tokenResponse.getError() != null) {
                log.error("❌ Google OAuth error: {} - {}", tokenResponse.getError(),
                        tokenResponse.getErrorDescription());
                throw new AppException(ErrorCode.OAUTH2_ERROR);
            }

            // Bước 2: Verify & parse ID token
            GoogleUserInfo userInfo = verifyAndParseIdToken(tokenResponse.getIdToken());

            // Bước 3: Tìm hoặc tạo user
            User user = findOrCreateOAuthUser(userInfo);

            // Bước 4: Generate JWT tokens
            return generateOAuthTokens(user.getUsername(), AuthProvider.GOOGLE.getValue());

        } catch (IOException e) {
            log.error("❌ Google OAuth token exchange error: {}", e.getMessage());
            throw new AppException(ErrorCode.OAUTH2_ERROR);
        } catch (Exception e) {
            log.error("❌ Google token verification error: {}", e.getMessage());
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }
    }

    /**
     * ✅ Generate JWT tokens cho OAuth users
     */
    @Override
    @Transactional
    public AuthenticateResponse generateOAuthTokens(String username, String provider) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));

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

        log.info("✅ User {} logged in via OAuth2 provider: {}", user.getUsername(), provider);

        return AuthenticateResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(userMap.toResponse(user))
                .role(jwtUtils.getStringRole(user.getRoles()))
                .provider(provider)
                .tokenType("Bearer")
                .expiresIn((int)(TIME_ACCESS * 60))  // Convert to seconds
                .build();
    }

    /**
     * ✅ Trao đổi authorization code lấy access token + ID token từ Google
     */
    private GoogleTokenResponse exchangeCodeForToken(String code) throws IOException {
        OkHttpClient client = new OkHttpClient();

        // ✅ DEBUG: Log code
        log.info("🔄 Exchange code for token:");
        log.info("   Code (first 20 chars): {}", code.substring(0, Math.min(20, code.length())));
        log.info("   Code length: {}", code.length());
        log.info("   Client ID: {}", oauth2Properties.getClientId().substring(0, 20) + "...");
        log.info("   Redirect URI: {}", oauth2Properties.getRedirectUri());
        log.info("   Token URL: {}", oauth2Properties.getTokenUrl());

        String requestBody = "code=" + code +
                "&client_id=" + oauth2Properties.getClientId() +
                "&client_secret=" + oauth2Properties.getClientSecret() +
                "&redirect_uri=" + oauth2Properties.getRedirectUri() +
                "&grant_type=authorization_code";

        // ✅ DEBUG: Log full request (without secret for security)
        String safeBody = "code=" + code.substring(0, Math.min(20, code.length())) + "..." +
                "&client_id=" + oauth2Properties.getClientId() +
                "&client_secret=***HIDDEN***" +
                "&redirect_uri=" + oauth2Properties.getRedirectUri() +
                "&grant_type=authorization_code";
        log.debug("   Request body: {}", safeBody);

        Request request = new Request.Builder()
                .url(oauth2Properties.getTokenUrl())
                .post(RequestBody.create(requestBody, okhttp3.MediaType.parse("application/x-www-form-urlencoded")))
                .addHeader("Accept", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            // ✅ FIX: Read body once
            String responseBody = response.body() != null ? response.body().string() : "";

            log.info("📥 Google response status: {}", response.code());

            if (!response.isSuccessful()) {
                log.error("❌ Failed to exchange token. Status: {}, Body: {}", response.code(), responseBody);
                throw new IOException("Failed to exchange token. Status: " + response.code() + " - " + responseBody);
            }

            log.debug("✅ Token exchange successful");

            GoogleTokenResponse tokenResponse = gson.fromJson(responseBody, GoogleTokenResponse.class);

            if (tokenResponse == null || tokenResponse.getIdToken() == null) {
                log.error("❌ Invalid token response from Google");
                throw new IOException("No ID token in response from Google");
            }

            log.info("✅ Got ID token: {} chars", tokenResponse.getIdToken().length());
            return tokenResponse;
        }
    }

    /**
     * ✅ Verify & parse Google ID token
     * - Verify chữ ký từ Google certificates
     * - Verify audience (client ID)
     * - Extract user info từ payload
     */
    private GoogleUserInfo verifyAndParseIdToken(String idToken) throws Exception {
        try {
            HttpTransport transport = new NetHttpTransport();
            JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

            // Tạo verifier với client ID
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                    .setAudience(Collections.singletonList(oauth2Properties.getClientId()))
                    .build();

            // Verify token signature & audience
            com.google.api.client.googleapis.auth.oauth2.GoogleIdToken idTokenObj = verifier.verify(idToken);
            if (idTokenObj == null) {
                log.error("❌ Invalid ID token signature or audience mismatch");
                throw new IllegalArgumentException("Invalid ID token signature");
            }

            com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload payload = idTokenObj.getPayload();

            // Verify email_verified (recommended)
            Boolean emailVerified = (Boolean) payload.getOrDefault("email_verified", false);
            if (!emailVerified) {
                log.warn("⚠️ Email not verified for user: {}", payload.get("email"));
            }

            // Extract user info từ token payload
            GoogleUserInfo userInfo = GoogleUserInfo.builder()
                    .sub((String) payload.get("sub"))
                    .email((String) payload.get("email"))
                    .name((String) payload.get("name"))
                    .givenName((String) payload.get("given_name"))
                    .familyName((String) payload.get("family_name"))
                    .picture((String) payload.get("picture"))
                    .emailVerified(emailVerified)
                    .locale((String) payload.get("locale"))
                    .build();

            log.debug("✅ ID token verified successfully for user: {}", userInfo.getEmail());
            return userInfo;

        } catch (Exception e) {
            log.error("❌ Error verifying ID token: {}", e.getMessage());
            throw new Exception("Token verification failed: " + e.getMessage(), e);
        }
    }

    /**
     * ✅ Tìm hoặc tạo user OAuth
     * Logic:
     * 1. Kiểm tra user tồn tại với (provider + providerId)
     * 2. Kiểm tra email tồn tại từ provider khác → link provider
     * 3. Tạo user mới
     */
    @Transactional
    private User findOrCreateOAuthUser(GoogleUserInfo userInfo) {
        // Kiểm tra 1: User đã tồn tại với provider + providerId (Google: sub)
        User existingUserWithProvider = userRepository.findByAuthProviderAndProviderId(
                AuthProvider.GOOGLE,
                userInfo.getSub()
        ).orElse(null);

        if (existingUserWithProvider != null) {
            // ✅ Update thông tin mới nhất từ Google
            existingUserWithProvider.setEmail(userInfo.getEmail());
            existingUserWithProvider.setFullName(userInfo.getName());
            existingUserWithProvider.setAvatar(userInfo.getPicture());
            existingUserWithProvider.setActive(true);
            existingUserWithProvider.setLastLoginAt(LocalDateTime.now());

            User updatedUser = userRepository.save(existingUserWithProvider);
            log.info("✅ OAuth user updated: {} ({})", existingUserWithProvider.getUsername(), existingUserWithProvider.getEmail());
            return updatedUser;
        }

        // Kiểm tra 2: Email đã tồn tại từ provider khác (LOCAL hoặc OAuth khác)
        User existingEmailUser = userRepository.findByEmail(userInfo.getEmail()).orElse(null);

        if (existingEmailUser != null) {
            // ✅ Link OAuth provider với existing account
            existingEmailUser.setAuthProvider(AuthProvider.GOOGLE);
            existingEmailUser.setProviderId(userInfo.getSub());
            existingEmailUser.setActive(true);
            existingEmailUser.setLastLoginAt(LocalDateTime.now());

            // Update other fields if null
            if (existingEmailUser.getFullName() == null || existingEmailUser.getFullName().isEmpty()) {
                existingEmailUser.setFullName(userInfo.getName());
            }
            if (existingEmailUser.getAvatar() == null || existingEmailUser.getAvatar().isEmpty()) {
                existingEmailUser.setAvatar(userInfo.getPicture());
            }

            User linkedUser = userRepository.save(existingEmailUser);
            log.info("✅ OAuth provider linked to existing account: {}", existingEmailUser.getUsername());
            return linkedUser;
        }

        // Kiểm tra 3: Tạo user mới
        String username = generateUsernameFromEmail(userInfo.getEmail());

        User newUser = User.builder()
                .publicId(UUID.randomUUID().toString())
                .username(username)
                .email(userInfo.getEmail())
                .fullName(userInfo.getName())
                .avatar(userInfo.getPicture())
                .authProvider(AuthProvider.GOOGLE)
                .providerId(userInfo.getSub())
                .password(null) // OAuth users không cần password
                .isActive(true)
                .enabled(true)
                .lastLoginAt(LocalDateTime.now())
                .build();

        // ✅ Gán role mặc định "customer"
        Role customerRole = roleRepository.findByName("customer")
                .orElseThrow(() ->  new AppException(ErrorCode.ROLE_NOT_EXIST));

        newUser.setRoles(Collections.singleton(customerRole));

        User savedUser = userRepository.save(newUser);
        log.info("✅ New OAuth user created: {} ({})", savedUser.getUsername(), savedUser.getEmail());

        return savedUser;
    }

    /**
     * ✅ Sinh username từ email (tránh conflict)
     * Logic:
     * 1. Lấy phần trước @ của email làm base username
     * 2. Nếu tồn tại, thêm random suffix (5 ký tự random)
     * 3. Max retry 5 lần
     */
    private String generateUsernameFromEmail(String email) {
        String baseUsername = email.substring(0, email.indexOf("@")).toLowerCase();

        // Kiểm tra username có tồn tại không
        if (!userRepository.existsByUsername(baseUsername)) {
            log.debug("✅ Username available: {}", baseUsername);
            return baseUsername;
        }

        log.debug("⚠️ Username {} already exists, generating alternative", baseUsername);

        // Nếu tồn tại, thêm random suffix
        String username;
        int attempt = 0;
        do {
            username = baseUsername + "_" + UUID.randomUUID().toString().substring(0, 5);
            attempt++;
        } while (userRepository.existsByUsername(username) && attempt < 5);

        if (attempt >= 5) {
            log.error("❌ Failed to generate unique username after 5 attempts");
            throw new AppException(ErrorCode.USER_NOT_EXIST);
        }

        log.debug("✅ Generated alternative username: {}", username);
        return username;
    }
}