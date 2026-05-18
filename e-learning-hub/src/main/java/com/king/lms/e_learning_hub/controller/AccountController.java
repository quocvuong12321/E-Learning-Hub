package com.king.lms.e_learning_hub.controller;

import org.springframework.web.bind.annotation.*;

import com.king.lms.e_learning_hub.dto.Response.ApiResponse;
import com.king.lms.e_learning_hub.dto.authenticate.AuthenticateResponse;
import com.king.lms.e_learning_hub.dto.user.ChangePasswordRequest;
import com.king.lms.e_learning_hub.dto.user.UserRequest;
import com.king.lms.e_learning_hub.dto.user.UserResponse;
import com.king.lms.e_learning_hub.dto.user.UserUpdateRequest;
import com.king.lms.e_learning_hub.enums.TokenType;
import com.king.lms.e_learning_hub.service.AccountManagementService;
import com.king.lms.e_learning_hub.service.BaseAuthenticationService;
import com.king.lms.e_learning_hub.service.JwtAuthenticationService;
import com.king.lms.e_learning_hub.util.CookieUtils;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/account")
public class AccountController {
    JwtAuthenticationService jwtAuthenticationService;
    AccountManagementService accountService;
    CookieUtils cookieUtils;
    @PostMapping("/register")
    public ApiResponse<UserResponse> register(@Valid @RequestBody UserRequest request){

        return ApiResponse.<UserResponse>builder()
                .result(jwtAuthenticationService.register(request))
                .build();
    }

    @GetMapping
    public ApiResponse<UserResponse> getProfile(){
        return ApiResponse.<UserResponse>builder()
                .result(accountService.getProfile())
                .build();
    }

    @PutMapping
    public ApiResponse<UserResponse> updateProfile(@RequestBody UserUpdateRequest request){


        return ApiResponse.<UserResponse>builder()
                .result(accountService.updateAccount(request))
                .build();

    }

    @PatchMapping("/change-password")
    public ApiResponse<AuthenticateResponse> changePassword(@Valid @RequestBody ChangePasswordRequest request, HttpServletResponse httpServletResponse){

        AuthenticateResponse response = accountService.changePassword(request);

        cookieUtils.saveCookie(TokenType.refresh.name(), response.getRefreshToken(), (int) (BaseAuthenticationService.TIME_ACCESS*60),httpServletResponse);

        return ApiResponse.<AuthenticateResponse>builder()
                .result(response)
                .build();
    }

}
