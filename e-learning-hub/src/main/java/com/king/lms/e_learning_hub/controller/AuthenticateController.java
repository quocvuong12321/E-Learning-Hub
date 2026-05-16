package com.king.lms.e_learning_hub.controller;


import com.king.lms.e_learning_hub.dto.Response.ApiResponse;
import com.king.lms.e_learning_hub.dto.authenticate.AccessTokenResponse;
import com.king.lms.e_learning_hub.dto.authenticate.AuthenticateRequest;
import com.king.lms.e_learning_hub.dto.authenticate.AuthenticateResponse;
import com.king.lms.e_learning_hub.dto.authenticate.RefreshTokenRequest;
import com.king.lms.e_learning_hub.enums.TokenType;
import com.king.lms.e_learning_hub.service.JwtAuthenticationService;
import com.king.lms.e_learning_hub.util.CookieUtils;
import com.nimbusds.jose.JOSEException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthenticateController {

    JwtAuthenticationService jwtAuthenticationService;
    CookieUtils cookieUtils;


    @PostMapping("/login")
    public ApiResponse<AuthenticateResponse> JwtLogin(@RequestBody AuthenticateRequest request, HttpServletResponse httpServletResponse){

        AuthenticateResponse response = jwtAuthenticationService.login(request);

        cookieUtils.saveCookie(TokenType.refresh.name(),response.getRefreshToken(), (int)JwtAuthenticationService.TIME_REFRESH*60,httpServletResponse);

        response.setRefreshToken("");

        return ApiResponse.<AuthenticateResponse>builder()
                .result(response)
                .build();
    }

    @PostMapping("/refresh")
    public ApiResponse<AccessTokenResponse> refreshToken(HttpServletRequest request) throws ParseException, JOSEException {
        Cookie cookie = cookieUtils.getCookie(TokenType.refresh.name(), request);

        String refreshToken = cookie.getValue();

        AccessTokenResponse response = jwtAuthenticationService.RefreshToken(RefreshTokenRequest.builder()
                        .refreshToken(refreshToken)
                .build());

        return ApiResponse.<AccessTokenResponse>builder()
                .result(response)
                .build();
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpServletResponse response) throws ParseException, JOSEException {

        jwtAuthenticationService.logout();

        cookieUtils.deleteCookie(TokenType.refresh.name(),response);

        return ApiResponse.<Void>builder()
                .build();

    }


//    /**
//     * ✅ OAuth2 Login
//     */
//    @PostMapping("/oauth2/login")
//    public ApiResponse<OAuthLoginResponse> oauthLogin(
//            @RequestBody OAuthLoginRequest request) {
//        OAuthLoginResponse response = jwtAuthenticationService.loginWithOAuth(request);
//        return ApiResponse.<OAuthLoginResponse>builder()
//        .result(response)
//        .build();
//
//    }

}
