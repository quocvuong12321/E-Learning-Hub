package com.king.lms.e_learning_hub.dto.user;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {

    String email;
    String fullName;
    String avatar = null;


}
