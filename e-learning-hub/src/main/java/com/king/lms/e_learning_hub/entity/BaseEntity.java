package com.king.lms.e_learning_hub.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.cglib.core.Local;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.LocalDateTime;

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
