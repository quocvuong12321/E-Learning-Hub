package com.king.lms.e_learning_hub.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class AppException extends RuntimeException{

    private final ErrorCode errorCode;

}
