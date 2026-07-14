package com.king.lms.e_learning_hub.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.king.lms.e_learning_hub.dto.Response.PageResponse;
import com.king.lms.e_learning_hub.dto.user.UserResponse;
import com.king.lms.e_learning_hub.entity.User;
import com.king.lms.e_learning_hub.exception.AppException;
import com.king.lms.e_learning_hub.exception.ErrorCode;
import com.king.lms.e_learning_hub.mapper.UserMapper;
import com.king.lms.e_learning_hub.repository.UserRepository;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {

   UserRepository userRepository;
    UserMapper userMapper;


    /**
     * Tìm kiếm và lọc user theo nhiều tiêu chí
     * - search: tìm kiếm theo username, email, fullName, phoneNumber
     * - isActive: lọc theo trạng thái hoạt động (true/false/null)
     */
    public PageResponse<UserResponse> searchAndFilterUsers(int page, int size, String search, Boolean isActive) {
        // Xử lý search string rỗng
        if (search != null && search.trim().isEmpty()) {
            search = null;
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<User> pageResult = userRepository.searchAndFilterUsers(search, isActive, pageable);

        return PageResponse.<UserResponse>builder()
                .currentPage(page)
                .totalPages(pageResult.getTotalPages())
                .pageSize(pageResult.getSize())
                .totalElements(pageResult.getTotalElements())
                .data(pageResult.stream().map(userMapper::toResponse).collect(Collectors.toList()))
                .build();
    }

    /**
     * Lấy user theo ID
     */
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));
        return userMapper.toResponse(user);
    }

    /**
     * Deactivate user (khoá tài khoản)
     */
    @Transactional
    public UserResponse deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));

        user.setActive(false);
        return userMapper.toResponse(userRepository.save(user));
    }

    /**
     * Activate user (mở khoá tài khoản)
     */
    @Transactional
    public UserResponse activateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));

        user.setActive(true);
        return userMapper.toResponse(userRepository.save(user));
    }

    // /**
    //  * Xoá user (admin)
    //  */
    // @Transactional
    // public void deleteUser(Long id) {
    //     User user = userRepository.findById(id)
    //             .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));

    //     // Xóa avatar
    //     if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
    //         fileUploadUtils.deleteImage(user.getAvatar());
    //     }

    //     userRepository.delete(user);
    // }

    // /**
    //  * Reset mật khẩu user (admin)
    //  */
    // @Transactional
    // public void resetUserPassword(Long id, String newPassword) {
    //     User user = userRepository.findById(id)
    //             .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));

    //     user.setPassword(passwordEncoder.encode(newPassword));
    //     userRepository.save(user);
    // }

    /**
     * Đếm tổng số user
     */
    public Long countTotalUsers() {
        return userRepository.count();
    }

    /**
     * Đếm user hoạt động
     */
    public Long countActiveUsers() {
        return userRepository.countByIsActiveTrue();
    }

    /**
     * Tự động deactivate user chưa đăng nhập trong 90 ngày
     * Chạy hàng ngày vào lúc 2:00 AM
     */
    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void deactivateInactiveUsers() {
        LocalDateTime ninetyDaysAgo = LocalDateTime.now().minusDays(90);
        List<User> inactiveUsers = userRepository.findInactiveUsersBefore90Days(ninetyDaysAgo);

        if (!inactiveUsers.isEmpty()) {
            inactiveUsers.forEach(user -> user.setActive(false));
            userRepository.saveAll(inactiveUsers); 
            log.info("Deactivated {} inactive users", inactiveUsers.size());
        }
    }
}