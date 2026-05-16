package com.king.lms.e_learning_hub.dto.user;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.print.DocFlavor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRequest {

    @NotBlank(message = "USERNAME_NOT_BLANK")
    String username;
    @Size(min = 8, message = "PASSWORD_LENGTH")
            @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–{}:;',?/*~$^+=<>]).{8,}$",
            message = "PASSWORD_INVALID"
            )
    String password;
    String confirmPassword;
    String email;
    String fullName;
    String avatar = null;
    String phoneNumber;
}
