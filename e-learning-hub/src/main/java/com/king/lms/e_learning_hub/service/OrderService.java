package com.king.lms.e_learning_hub.service;

import org.springframework.stereotype.Service;

import com.king.lms.e_learning_hub.dto.order.OrderResponse;
import com.king.lms.e_learning_hub.entity.Order;
import com.king.lms.e_learning_hub.entity.Order.OrderStatus;
import com.king.lms.e_learning_hub.exception.AppException;
import com.king.lms.e_learning_hub.exception.ErrorCode;
import com.king.lms.e_learning_hub.mapper.OrderMapper;
import com.king.lms.e_learning_hub.repository.OrderRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class OrderService {

    OrderRepository orderRepository;
    OrderMapper orderMapper;

    public OrderResponse updateStatusOrder(Long orderId, String status){
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXIST));

        OrderStatus orderStatus = OrderStatus.valueOf(status);

        order.setStatus(orderStatus);
        
        return orderMapper.toResponse(orderRepository.save(order));
    }

     
}
