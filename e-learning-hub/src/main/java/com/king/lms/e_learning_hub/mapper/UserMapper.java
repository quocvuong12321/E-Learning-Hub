package com.king.lms.e_learning_hub.mapper;


import org.mapstruct.Mapper;

import com.king.lms.e_learning_hub.dto.user.UserRequest;
import com.king.lms.e_learning_hub.dto.user.UserResponse;
import com.king.lms.e_learning_hub.entity.User;

@Mapper(componentModel = "Spring")
public interface UserMapper {

    UserResponse toResponse(User user);

    User toUser(UserRequest request);

}
