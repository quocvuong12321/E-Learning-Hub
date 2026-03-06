package com.king.lms.e_learning_hub.dto.Response;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApiResponse<T> {

//    @Builder.Default
    int code = 1000;
//    @Builder.Default
    String message = "Successfully";
    T result;
//    @Builder.Default
    int status = 200;

}
