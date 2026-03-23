package com.king.lms.e_learning_hub.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum ErrorCode {
    // 1xxx: bad request (Lỗi dữ liệu đầu vào)
    USERNAME_INVALID(1001,"tai khoan khong hop le",HttpStatus.BAD_REQUEST),
    UNCATEGORIZED_VALIDATION(1999, "Uncategorezed validation", HttpStatus.BAD_REQUEST),
    // 2xxx: auth (Lỗi xác thực và ủy quyền)
    UNAUTHENTICATED(2001,"Chưa được xác thực/Đăng nhập",HttpStatus.UNAUTHORIZED),
    EXPIRED_TOKEN(2002,"Token đã hết hạn",HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN(2004,"Token không hợp lệ", HttpStatus.UNAUTHORIZED),
    // 3xxx: business logic (Lỗi nghiệp vụ)
    ROLE_NOT_EXIST(3001,"Vai tro khong ton tai",HttpStatus.NOT_FOUND),
    USER_NOT_EXIST(3002,"Tai khoan khong ton tai",HttpStatus.NOT_FOUND),
    PASSWORD_INCORRECT(3003,"Mat khau khong chinh xac", HttpStatus.BAD_REQUEST),
    // 4xxx: Lỗi chung khi vi phạm ràng buộc DB (ví dụ: để fallback)

    // 9xxx: system (Lỗi hệ thống)
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),

    ;

    private int code;
    private String message;
    private HttpStatusCode statusCode;

}
