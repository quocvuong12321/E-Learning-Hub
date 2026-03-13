package com.king.lms.e_learning_hub.entity;


import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass // Đánh dấu đây là lớp cha, không tạo bảng riêng trong DB
@EntityListeners(AuditingEntityListener.class) //Kích hoạt tự động lắng nghe sự kiện JPA
@Getter
@Setter
public class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    LocalDateTime createdAt;

    @LastModifiedDate
    @Column(insertable = false)
    LocalDateTime updatedAt;

    //Để các trường @CreatedDate tự động hoạt động,
    // bạn phải thêm annotation @EnableJpaAuditing vào một file cấu hình hoặc file Main.

}
