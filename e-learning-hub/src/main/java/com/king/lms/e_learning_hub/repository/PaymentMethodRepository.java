package com.king.lms.e_learning_hub.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.king.lms.e_learning_hub.entity.PaymentMethod;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod,Long>{

    Optional<PaymentMethod> findByCode(String code);
    

}
