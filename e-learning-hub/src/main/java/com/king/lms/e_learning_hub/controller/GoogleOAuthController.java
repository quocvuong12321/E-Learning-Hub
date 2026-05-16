package com.king.lms.e_learning_hub.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.king.lms.e_learning_hub.dto.Response.ApiResponse;
import com.king.lms.e_learning_hub.dto.authenticate.AuthenticateResponse;
import com.king.lms.e_learning_hub.dto.oauth2.GoogleOAuthConfigResponse;
import com.king.lms.e_learning_hub.dto.oauth2.OAuthLoginRequest;
import com.king.lms.e_learning_hub.service.JwtAuthenticationService;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@RestController
@RequestMapping("/auth/oauth2")
@AllArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

public class GoogleOAuthController {

    JwtAuthenticationService jwtAuthenticationService;

    /**
     * ✅ Get Google OAuth configuration
     * Frontend calls this to get the authorization URL
     *
     * GET /auth/oauth2/config
     * Response: {
     *   "code": 1000,
     *   "result": {
     *     "clientId": "...",
     *     "redirectUri": "...",
     *     "scope": "openid email profile",
     *     "responseType": "code",
     *     "authorizationUrl": "https://accounts.google.com/o/oauth2/v2/auth?..."
     *   }
     * }
     */
    @GetMapping("/login-url")
    public ApiResponse<GoogleOAuthConfigResponse> getGoogleConfig() {
        log.info("📨 Google OAuth config request received");

        try {
            GoogleOAuthConfigResponse config = jwtAuthenticationService.getGoogleOAuthConfig();

            log.info("✅ Google OAuth config provided");

            return ApiResponse.<GoogleOAuthConfigResponse>builder()
                    .result(config)
                    .build();
        } catch (Exception e) {
            log.error("❌ Error getting Google OAuth config: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * ✅ Handle Google OAuth login
     * Frontend gửi authorization code → backend exchange token → create/update user → return JWT
     * 
     * POST /auth/oauth2/login
     * Body: { "code": "..." }
     * Response: { "result": { "accessToken", "refreshToken", "user", "provider" } }
     */
    @PostMapping("/login")
    public ApiResponse<AuthenticateResponse> loginWithGoogle(
            @RequestBody OAuthLoginRequest request) {
        
        log.info("📨 Google OAuth login request received");

        AuthenticateResponse response = jwtAuthenticationService.loginWithOAuth(request);
        
        log.info("✅ Google OAuth login successful for user: {}", response.getUser().getUsername());
        
        return ApiResponse.<AuthenticateResponse>builder()
                .result(response)
                .build();
    }

    /**
     * ✅ Google Callback endpoint (alternative)
     * Nếu frontend redirect trực tiếp tới backend
     * 
     * GET /auth/oauth2/callback?code=...&state=...
     * Response: { "result": { "accessToken", "refreshToken", "user", "provider" } }
     */
    @GetMapping("/callback")
    public void googleCallback(
            @RequestParam String code,
            @RequestParam(required = false) String state,
            HttpServletResponse response) throws IOException {

        log.info("📨 Google callback received with code: {}", code.substring(0, 10) + "...");

        OAuthLoginRequest request = OAuthLoginRequest.builder()
                .code(code)
                .state(state)
                .build();

        AuthenticateResponse authResponse = jwtAuthenticationService.loginWithOAuth(request);

        // ✅ Redirect về frontend với token trong query params
        String redirectUrl = String.format(
                "http://localhost:5173/auth/google/callback?token=%s&role=%s",
                authResponse.getAccessToken(),
                authResponse.getRole()
        );

        log.info("✅ Redirecting to: {}", redirectUrl);
        response.sendRedirect(redirectUrl);
    }


    /**
     * ✅ Health check endpoint
     * GET /auth/oauth2/health
     */
    @GetMapping("/health")
    public ApiResponse<String> health() {
        log.info("✅ Google OAuth service health check");
        return ApiResponse.<String>builder()
                .result("Google OAuth service is running")
                .build();
    }
}