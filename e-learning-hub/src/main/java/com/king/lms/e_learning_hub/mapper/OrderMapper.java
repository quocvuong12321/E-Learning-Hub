package com.king.lms.e_learning_hub.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.king.lms.e_learning_hub.dto.order.OrderResponse;
import com.king.lms.e_learning_hub.entity.Order;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "course", target = "course")
    @Mapping(target = "paymentMethod",ignore=true)
    @Mapping(source = "status", target = "status")
    OrderResponse toResponse(Order order);
}