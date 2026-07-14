package com.king.lms.e_learning_hub.controller;

import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.king.lms.e_learning_hub.dto.Response.ApiResponse;
import com.king.lms.e_learning_hub.dto.order.OrderResponse;
import com.king.lms.e_learning_hub.service.OrderService;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@AllArgsConstructor
@RequestMapping("/order")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderController {
    OrderService orderService;

    @PatchMapping("/{id}")
    public ApiResponse<OrderResponse> updateStatusOrder(@PathVariable Long id, @RequestParam String status){
        return ApiResponse.<OrderResponse>builder()
        .result(orderService.updateStatusOrder(id, status))
        .build();
    }
}
