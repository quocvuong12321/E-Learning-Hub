package com.king.lms.e_learning_hub.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.king.lms.e_learning_hub.enums.AuthProvider;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Setter
@Table(name = "users")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User extends BaseEntity{

    @Column(unique = true, length = 36, nullable = false)
    String publicId;

    // Chuyển sang nullable = true để hỗ trợ đăng nhập Google nhanh
    @Column(unique = true, nullable = true) 
    String username;

    // Nullable vì OAuth2 không dùng password hệ thống
    @Column(nullable = true)
    String password;

    @Column(unique = true, nullable = false)
    String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_provider")
    AuthProvider authProvider; // Enum tự định nghĩa

    // ID duy nhất từ Google (sub claim), cực kỳ quan trọng để đối soát
    @Column(name = "provider_id")
    String providerId;

    @Column(length = 128, nullable = false) // Tăng độ dài cho tên đầy đủ
    String fullName;
    
    // Lưu link ảnh từ Google để cá nhân hóa Dashboard ngay lập tức
    String avatar;
    
    @Column(length = 20)
    String phoneNumber;
    
    boolean enabled = true;

    boolean isActive = true;

    // Quan hệ với Roles để phục vụ phân quyền Admin/Học viên
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    Set<Role> roles;

    LocalDateTime lastLoginAt;

    // Phục vụ Blog cá nhân
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "author", cascade = CascadeType.ALL)
    List<Post> posts;

    // THÊM: Theo dõi các khóa học đã mua (Trái tim của LMS)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "users_courses",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    List<Course> enrolledCourses;

    @PrePersist
    protected void onCreate() {
        if (this.publicId == null) {
            this.publicId = java.util.UUID.randomUUID().toString();
        }
        // Luôn đảm bảo provider mặc định là LOCAL nếu không truyền vào
        if (this.authProvider == null) {
            this.authProvider = AuthProvider.LOCAL;
        }
    }
}