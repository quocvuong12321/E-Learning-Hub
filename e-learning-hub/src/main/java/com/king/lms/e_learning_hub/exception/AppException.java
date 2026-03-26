package com.king.lms.e_learning_hub.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class AppException extends RuntimeException{

    private ErrorCode errorCode;

}
