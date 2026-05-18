package com.king.lms.e_learning_hub.service;

import java.text.ParseException;

import com.king.lms.e_learning_hub.dto.authenticate.AccessTokenResponse;
import com.king.lms.e_learning_hub.dto.authenticate.AuthenticateRequest;
import com.king.lms.e_learning_hub.dto.authenticate.AuthenticateResponse;
import com.king.lms.e_learning_hub.dto.authenticate.RefreshTokenRequest;
import com.nimbusds.jose.JOSEException;

public interface BaseAuthenticationService {

    public static final long TIME_ACCESS = 20;       // 20 phút
    public static final long TIME_REFRESH = 20 * 24 * 60; // 20 ngày
    AuthenticateResponse login(AuthenticateRequest request);
    AccessTokenResponse RefreshToken(RefreshTokenRequest request) throws ParseException, JOSEException;;
    void logout() throws ParseException, JOSEException;
}
