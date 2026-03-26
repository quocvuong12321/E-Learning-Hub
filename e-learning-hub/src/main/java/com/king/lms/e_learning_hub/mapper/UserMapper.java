package com.king.lms.e_learning_hub.mapper;


import com.king.lms.e_learning_hub.dto.user.UserRequest;
import com.king.lms.e_learning_hub.dto.user.UserResponse;
import com.king.lms.e_learning_hub.entity.User;
import org.mapstruct.Mapper;
import org.springframework.web.bind.annotation.Mapping;

@Mapper(componentModel = "Spring")
public interface UserMapper {

    UserResponse toResponse(User user);

    User toUser(UserRequest request);

}
