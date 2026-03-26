package com.king.lms.e_learning_hub.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public enum ErrorCode {
    // 1xxx: bad request (Lỗi dữ liệu đầu vào)
    USERNAME_INVALID(1001,"Tài khoản không hợp lệ" ,HttpStatus.BAD_REQUEST),
    PASSWORD_NOT_MATCH(1002,"Mật khẩu không khớp",HttpStatus.BAD_REQUEST),
    USERNAME_NOT_BLANK(1003,"Tài khoản không được để trống",HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1004,"Password phải bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt",HttpStatus.BAD_REQUEST),
    PASSWORD_LENGTH(1005,"Password phải có ít nhất 8 ký tự",HttpStatus.BAD_REQUEST),
    UNCATEGORIZED_VALIDATION(1999, "Uncategorezed validation", HttpStatus.BAD_REQUEST),
    // 2xxx: auth (Lỗi xác thực và ủy quyền)
    UNAUTHENTICATED(2001,"Chưa được xác thực/Đăng nhập",HttpStatus.UNAUTHORIZED),
    EXPIRED_TOKEN(2002,"Token đã hết hạn",HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN(2004,"Token không hợp lệ", HttpStatus.UNAUTHORIZED),
    // 3xxx: business logic (Lỗi nghiệp vụ)
    ROLE_NOT_EXIST(3001,"Vai trò không tồn tại",HttpStatus.NOT_FOUND),
    USER_NOT_EXIST(3002,"Tài khoản không tồn tại",HttpStatus.NOT_FOUND),
    PASSWORD_INCORRECT(3003,"Mật khẩu không chính xác", HttpStatus.BAD_REQUEST),
    // 4xxx: Lỗi chung khi vi phạm ràng buộc DB (ví dụ: để fallback)
    USERNAME_EXISTED(4001,"Tài khoản đã tồn tại",HttpStatus.CONFLICT),
    EMAIL_EXISTED(4002,"Email đã tồn tại",HttpStatus.CONFLICT),
    // 9xxx: system (Lỗi hệ thống)
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),

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
