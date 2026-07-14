package com.king.lms.e_learning_hub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import com.king.lms.e_learning_hub.entity.Order;

import jakarta.persistence.LockModeType;

public interface OrderRepository extends JpaRepository<Order,Long>{

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Order findByCourseIdAndUserId(Long courseId, Long UserId);
    
}
