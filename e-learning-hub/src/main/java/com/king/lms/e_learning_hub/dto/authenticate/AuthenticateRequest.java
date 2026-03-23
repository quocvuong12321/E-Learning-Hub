package com.king.lms.e_learning_hub.dto.authenticate;


import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticateRequest {

    @Size(min = 5,message = "USERNAME_INVALID")
    String username;

    String password;

}
