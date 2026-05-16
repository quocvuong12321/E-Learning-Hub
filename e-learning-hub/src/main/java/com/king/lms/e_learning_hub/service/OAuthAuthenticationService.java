package com.king.lms.e_learning_hub.service;

import com.king.lms.e_learning_hub.dto.authenticate.AuthenticateResponse;
import com.king.lms.e_learning_hub.dto.oauth2.OAuthLoginRequest;

/**
 * Interface cho OAuth2 authentication (Google, Facebook, etc)
 */
public interface OAuthAuthenticationService {
    public static final long TIME_ACCESS = 20;
    public static final long TIME_REFRESH = 20 * 24 * 60;

    /**
     * Handle OAuth flow: exchange code → verify token → create/update user → generate JWT
     */
    AuthenticateResponse handleOAuth(OAuthLoginRequest request);

    /**
     * Generate JWT tokens cho OAuth users
     */
    AuthenticateResponse generateOAuthTokens(String username, String provider);
}