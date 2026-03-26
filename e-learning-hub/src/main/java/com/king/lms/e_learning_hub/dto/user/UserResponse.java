package com.king.lms.e_learning_hub.dto.user;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {

    String email;
    String username;
    String avatar;
    String fullName;
    String publicId;

}
