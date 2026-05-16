package com.king.lms.e_learning_hub.repository;

import com.king.lms.e_learning_hub.entity.User;
import com.king.lms.e_learning_hub.enums.AuthProvider;

import io.lettuce.core.dynamic.annotation.Param;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByPublicId(String publicId);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    /**
     * Tìm và lọc user theo nhiều tiêu chí
     * - search: tìm kiếm theo username, email, fullName, phoneNumber
     * - isActive: lọc theo trạng thái hoạt động
     */
    @Query("""
            SELECT u FROM User u
            WHERE (:search IS NULL OR 
                   LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')) OR
                   LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR
                   LOWER(u.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR
                   LOWER(u.phoneNumber) LIKE LOWER(CONCAT('%', :search, '%')))
              AND (:isActive IS NULL OR u.isActive = :isActive)
            ORDER BY u.id DESC
            """)
    Page<User> searchAndFilterUsers(
            @Param("search") String search,
            @Param("isActive") Boolean isActive,
            Pageable pageable);

    /**
     * Tìm user chưa đăng nhập trong 90 ngày và vẫn hoạt động
     */
    @Query("""
            SELECT u FROM User u 
            WHERE u.isActive = true 
            AND (u.lastLoginAt IS NULL OR u.lastLoginAt < :ninetyDaysAgo)
            """)
    List<User> findInactiveUsersBefore90Days(@Param("ninetyDaysAgo") LocalDateTime ninetyDaysAgo);

    /**
     * Lấy danh sách user hoạt động
     */
    Page<User> findByIsActiveTrue(Pageable pageable);

    /**
     * Đếm user hoạt động
     */
    Long countByIsActiveTrue();


    /**
     * Tìm user bằng OAuth provider + providerId
     */
    Optional<User> findByAuthProviderAndProviderId(AuthProvider authProvider, String providerId);

    /**
     * Tìm user bằng email
     */
    Optional<User> findByEmail(String email);

}
