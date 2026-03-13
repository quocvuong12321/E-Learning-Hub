package com.king.lms.e_learning_hub.exception;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.king.lms.e_learning_hub.dto.Response.ApiResponse;

import jakarta.validation.ConstraintViolation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice //đánh dau day la lop xu ly loi toan cuc
public class GlobalExceptionHandler {


    @ExceptionHandler(value = RuntimeException.class)
        // Loi khong xac dinh --> lay tu log exception
    ResponseEntity<ApiResponse<Object>> handlingRuntimeException(RuntimeException e) {
        ErrorCode errorCode = ErrorCode.UNCATEGORIZED_EXCEPTION;

        String messageToReturn = e.getMessage() != null && !e.getMessage().isBlank()
                ? e.getMessage()
                : errorCode.getMessage();

        ApiResponse<Object> response = ApiResponse.<Object>builder()
                .message(messageToReturn)
                .code(errorCode.getCode())
                .status(errorCode.getStatusCode().value())
                .build();

        return ResponseEntity
                .status(errorCode.getStatusCode().value())
                .body(response);

    }

    //Xử lý các lỗi do mình định nghĩa và chủ động throw ra (AppException)
    @ExceptionHandler(value = AppException.class)
    //
    ResponseEntity<ApiResponse<Object>> hanglingAppException(AppException e){
        ErrorCode errorCode = e.getErrorCode();

        ApiResponse<Object> response = ApiResponse.builder()
                .code(errorCode.getCode())
                .status(errorCode.getStatusCode().value())
                .message(errorCode.getMessage())
                .build();

        return ResponseEntity.
                status(errorCode.getStatusCode().value())
                .body(response);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse<Object>> handlingValidation(MethodArgumentNotValidException exception) {
        try {
            log.info("Handling validation exception: {}", exception.getMessage());
            List<Map<String, Object>> errors = new ArrayList<>();
            // Định nghĩa ErrorCode mặc định
            ErrorCode defaultErrorCode = ErrorCode.UNCATEGORIZED_VALIDATION;

            for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
                String enumKey = fieldError.getDefaultMessage();
                ErrorCode errorCode = defaultErrorCode;
                String errorMessageToReturn = enumKey; // Mặc định là message gốc
                Map<String, Object> attributes = null;

                try {
                    // Cố gắng map sang ErrorCode tùy chỉnh
                    if (enumKey != null) {
                        errorCode = ErrorCode.valueOf(enumKey);
                        var constraintViolation = fieldError.unwrap(ConstraintViolation.class);
                        attributes = constraintViolation.getConstraintDescriptor().getAttributes();

                        // Nếu map thành công, sử dụng message đã được mapAttributes
                        errorMessageToReturn = attributes != null
                                ? mapAttributes(errorCode.getMessage(), attributes)
                                : errorCode.getMessage();
                    }
                } catch (Exception e) {
                    // Không làm gì: errorCode giữ nguyên default (INVALID_KEY), errorMessageToReturn giữ nguyên message gốc
                    log.warn("Could not map field error message '{}' to custom ErrorCode. Returning original message.", enumKey);
                }

                Map<String, Object> errorDetail = new HashMap<>();
                errorDetail.put("field", fieldError.getField());
                errorDetail.put("code", errorCode.getCode());
                errorDetail.put("message", errorMessageToReturn); // Sử dụng message đã xác định

                errors.add(errorDetail);
            }

            ApiResponse<Object> apiResponse = new ApiResponse<>();
            apiResponse.setCode(defaultErrorCode.getCode());
            apiResponse.setMessage("Validation failed");
            apiResponse.setResult(errors);
            apiResponse.setStatus(400);

            return ResponseEntity.status(400).body(apiResponse);
        } catch (Exception e) {
            // Cập nhật lỗi fallback trong handler để trả về 500
            ErrorCode internalError = ErrorCode.UNCATEGORIZED_EXCEPTION;
            ApiResponse<Object> fallbackResponse = ApiResponse.builder()
                    .code(internalError.getCode())
                    .message("Internal validation handler error: " + e.getMessage())
                    .build();
            return ResponseEntity.status(internalError.getStatusCode()).body(fallbackResponse);
        }
    }

    private String mapAttributes(String message, Map<String, Object> attributes) {
        if (attributes == null)
            return message;

        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue().toString();
            message = message.replace("{" + key + "}", value);
        }
        return message;
    }


}