package com.king.lms.e_learning_hub.dto.user;


import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChangePasswordRequest {

    String oldPassword;

    @Size(min = 8, message = "PASSWORD_LENGTH")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–{}:;',?/*~$^+=<>]).{8,}$",
            message = "PASSWORD_INVALID"
    )
    String password;
    String confirmPassword;

}
