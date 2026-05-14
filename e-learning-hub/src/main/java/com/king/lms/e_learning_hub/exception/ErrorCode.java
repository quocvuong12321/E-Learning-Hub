package com.king.lms.e_learning_hub.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;

@Getter
public enum ErrorCode {
    // 1xxx: Bad Request (Input Validation Errors)
    USERNAME_INVALID(1001, "Invalid username format", HttpStatus.BAD_REQUEST),
    PASSWORD_NOT_MATCH(1002, "Passwords do not match", HttpStatus.BAD_REQUEST),
    USERNAME_NOT_BLANK(1003, "Username cannot be blank", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1004, "Password must contain uppercase, lowercase, numbers, and special characters", HttpStatus.BAD_REQUEST),
    PASSWORD_LENGTH(1005, "Password must be at least 8 characters long", HttpStatus.BAD_REQUEST),
    UNCATEGORIZED_VALIDATION(1999, "Uncategorized validation error", HttpStatus.BAD_REQUEST),

    // 2xxx: Auth (Authentication and Authorization Errors)
    UNAUTHENTICATED(2001, "User is not authenticated. Please log in.", HttpStatus.UNAUTHORIZED),
    EXPIRED_TOKEN(2002, "Token has expired", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN(2004, "Invalid token", HttpStatus.UNAUTHORIZED),

    // 3xxx: Business Logic (Service-level Errors)
    ROLE_NOT_EXIST(3001, "Role does not exist", HttpStatus.NOT_FOUND),
    USER_NOT_EXIST(3002, "User account not found", HttpStatus.NOT_FOUND),
    CATEGORY_NOT_EXIST(3003, "Category not found", HttpStatus.NOT_FOUND),
    PASSWORD_INCORRECT(3004, "Incorrect password", HttpStatus.BAD_REQUEST), // Sửa lại mã 3004 để tránh trùng với CATEGORY_NOT_EXIST
    POST_NOT_EXIST(3005,"Post does not exist",HttpStatus.NOT_FOUND),
    COURSE_NOT_EXIST(3006,"Post does not exist",HttpStatus.NOT_FOUND),
    
    // 4xxx: Database Constraints (Persistence Errors)
    USERNAME_EXISTED(4001, "Username already exists", HttpStatus.CONFLICT),
    EMAIL_EXISTED(4002, "Email already exists", HttpStatus.CONFLICT),
    SLUG_EXISTED(4003, "Slug already exists, please choose another one", HttpStatus.CONFLICT), // Bổ sung cho SEO Slug logic

    // 9xxx: System (Global Errors)
    UNCATEGORIZED_EXCEPTION(9999, "An unexpected system error occurred", HttpStatus.INTERNAL_SERVER_ERROR),
    ;

    private int code;
    private String message;
    private HttpStatusCode statusCode;

    // Tự viết constructor (không dùng Lombok)
    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
}
