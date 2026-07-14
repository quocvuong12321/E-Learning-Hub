package com.king.lms.e_learning_hub.exception;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AppException extends RuntimeException{

    private ErrorCode errorCode;
    String message = "Error";
    public AppException(ErrorCode errorCode,String message) {
        this.errorCode = errorCode;
        this.message = message;
    }
    public AppException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    
}
