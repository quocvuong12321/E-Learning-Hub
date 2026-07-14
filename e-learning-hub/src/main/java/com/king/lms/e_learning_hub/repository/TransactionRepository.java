package com.king.lms.e_learning_hub.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import com.king.lms.e_learning_hub.entity.Order;
import com.king.lms.e_learning_hub.entity.Transaction;
import com.king.lms.e_learning_hub.entity.Transaction.TransactionStatus;

import jakarta.persistence.LockModeType;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Long>{

    Transaction findByOrderId(Long orderId);

    Optional<Transaction> findByOrderAndStatusIn(Order order, List<Transaction.TransactionStatus> statuses);
    // Hoặc tìm theo OrderId và trạng thái PROCESSING
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Transaction> findByOrderIdAndStatus(Long orderId, TransactionStatus status);

    // Find stale processing transactions created before given time
    java.util.List<Transaction> findByStatusAndCreatedAtBefore(TransactionStatus status, java.time.LocalDateTime before);
}
