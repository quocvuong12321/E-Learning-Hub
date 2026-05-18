package com.king.lms.e_learning_hub.mapper;


import com.king.lms.e_learning_hub.dto.user.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.king.lms.e_learning_hub.dto.user.UserRequest;
import com.king.lms.e_learning_hub.entity.User;

@Mapper(componentModel = "Spring")
public interface UserMapper {

    @Mapping(source = "active", target = "isActive")  // ← Explicit mapping
    UserResponse toResponse(User user);

    User toUser(UserRequest request);

}
