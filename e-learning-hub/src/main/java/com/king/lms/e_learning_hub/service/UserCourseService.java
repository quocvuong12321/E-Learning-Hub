package com.king.lms.e_learning_hub.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.king.lms.e_learning_hub.dto.user_course.UserCourseRequest;
import com.king.lms.e_learning_hub.dto.user_course.UserCourseResponse;
import com.king.lms.e_learning_hub.dto.user_course.UserCourseUpdateRequest;
import com.king.lms.e_learning_hub.entity.Course;
import com.king.lms.e_learning_hub.entity.Order;
import com.king.lms.e_learning_hub.entity.User;
import com.king.lms.e_learning_hub.entity.UserCourse;
import com.king.lms.e_learning_hub.exception.AppException;
import com.king.lms.e_learning_hub.exception.ErrorCode;
import com.king.lms.e_learning_hub.mapper.UserCourseMapper;
import com.king.lms.e_learning_hub.repository.CourseRepository;
import com.king.lms.e_learning_hub.repository.OrderRepository;
import com.king.lms.e_learning_hub.repository.UserRepository;
import com.king.lms.e_learning_hub.repository.UserCourseRepository;
import com.king.lms.e_learning_hub.util.JwtUtils;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserCourseService {

    UserCourseRepository userCourseRepository;
    UserRepository userRepository;
    CourseRepository courseRepository;
    OrderRepository orderRepository;
    UserCourseMapper userCourseMapper;
    JwtUtils jwtUtils;

    // ==========================================
    // HÀM CREATE (Tạo mới)
    // ==========================================

    /**
     * Tạo mới một đăng ký khóa học cho người dùng (lấy user từ JWT token)
     */
    @Transactional
    public UserCourseResponse createUserCourse(UserCourseRequest request) {
        
        // Lấy User hiện tại từ JWT token
        Long userId = jwtUtils.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));

        // Kiểm tra Course tồn tại
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_EXIST));

        // Kiểm tra User chưa đăng ký khóa học này
        userCourseRepository.findByUserIdAndCourseId(user.getId(), course.getId())
                .ifPresent(uc -> {
                    throw new AppException(ErrorCode.USER_ALREADY_ENROLLED_COURSE);
                });

        // Map từ request sang entity
        UserCourse userCourse = userCourseMapper.toEntity(request);
        userCourse.setUser(user);
        userCourse.setCourse(course);
        userCourse.setEnrolledAt(LocalDateTime.now());
        userCourse.setStatus(UserCourse.UserCourseStatus.ACTIVE);

        // Nếu có orderId, kiểm tra và gán Order
        if (request.getOrderId() != null) {
            Order order = orderRepository.findById(request.getOrderId())
                    .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXIST));
            userCourse.setOrder(order);
        }

        // Map EnrollmentType từ String sang Enum
        userCourse.setEnrollmentType(
            UserCourse.EnrollmentType.valueOf(request.getEnrollmentType())
        );

        // Lưu và trả về response
        return userCourseMapper.toResponse(userCourseRepository.save(userCourse));
    }

    /**
     * Tạo đăng ký khóa học từ một order đã thanh toán thành công.
     * Dùng cho luồng callback payment, không phụ thuộc JWT.
     * Nếu user đã đăng ký rồi thì trả về bản ghi hiện có để đảm bảo idempotent.
     */
    @Transactional
    public UserCourseResponse createUserCourseFromPaidOrder(Order order) {
        if (order == null || order.getUser() == null || order.getCourse() == null) {
            throw new AppException(ErrorCode.ORDER_NOT_EXIST);
        }

        return userCourseRepository.findByUserIdAndCourseId(order.getUser().getId(), order.getCourse().getId())
                .map(userCourseMapper::toResponse)
                .orElseGet(() -> {
                    UserCourse userCourse = UserCourse.builder()
                            .user(order.getUser())
                            .course(order.getCourse())
                            .order(order)
                            .enrolledAt(LocalDateTime.now())
                            .status(UserCourse.UserCourseStatus.ACTIVE)
                            .enrollmentType(UserCourse.EnrollmentType.PAID)
                            .build();

                    return userCourseMapper.toResponse(userCourseRepository.save(userCourse));
                });
    }

    // ==========================================
    // HÀM GET (Lấy chi tiết / danh sách)
    // ==========================================

    /**
     * Lấy chi tiết một UserCourse
     */
    public UserCourseResponse getUserCourseById(Long userCourseId) {
        UserCourse userCourse = userCourseRepository.findById(userCourseId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_COURSE_NOT_EXIST));
        return userCourseMapper.toResponse(userCourse);
    }

    /**
     * Lấy toàn bộ khóa học của User hiện tại (lấy từ JWT token)
     */
    public List<UserCourseResponse> getUserCoursesByUserId() {
        Long userId = jwtUtils.getCurrentUserId();
        
        // Kiểm tra User tồn tại
        userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));

        List<UserCourse> userCourses = userCourseRepository.findByUserId(userId);
        return userCourses.stream()
                .map(userCourseMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Lấy toàn bộ Users đã đăng ký một Course (chỉ admin)
     */
    public List<UserCourseResponse> getUsersByCourseId(Long courseId) {
        // Kiểm tra Course tồn tại
        courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_EXIST));

        List<UserCourse> userCourses = userCourseRepository.findByCourseId(courseId);
        return userCourses.stream()
                .map(userCourseMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Kiểm tra User đã đăng ký Course chưa
     */
    public Boolean isUserEnrolledInCourse(Long courseId) {
        Long userId = jwtUtils.getCurrentUserId();
        return userCourseRepository.findByUserIdAndCourseId(userId, courseId).isPresent();
    }

    /**
     * Lấy UserCourse của User hiện tại trong một Course
     */
    public UserCourseResponse getUserCourseByUserAndCourse(Long courseId) {
        Long userId = jwtUtils.getCurrentUserId();
        UserCourse userCourse = userCourseRepository.findByUserIdAndCourseId(userId, courseId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_COURSE_NOT_EXIST));
        return userCourseMapper.toResponse(userCourse);
    }

    // ==========================================
    // HÀM UPDATE (Cập nhật)
    // ==========================================

    /**
     * Cập nhật trạng thái UserCourse (ACTIVE -> EXPIRED, v.v.)
     * Chỉ admin có thể cập nhật
     */
    @Transactional
    public UserCourseResponse updateUserCourseStatus(Long userCourseId, UserCourseUpdateRequest request) {
        UserCourse userCourse = userCourseRepository.findById(userCourseId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_COURSE_NOT_EXIST));

        userCourse.setStatus(UserCourse.UserCourseStatus.valueOf(request.getStatus()));
        return userCourseMapper.toResponse(userCourseRepository.save(userCourse));
    }

    /**
     * Đánh dấu khóa học của User hiện tại đã hoàn thành
     */
    @Transactional
    public UserCourseResponse completeUserCourse(Long courseId) {
        Long userId = jwtUtils.getCurrentUserId();
        
        UserCourse userCourse = userCourseRepository.findByUserIdAndCourseId(userId, courseId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_COURSE_NOT_EXIST));

        if (userCourse.isCompleted()) {
            throw new AppException(ErrorCode.USER_COURSE_ALREADY_COMPLETED);
        }

        userCourse.setCompletedAt(LocalDateTime.now());
        return userCourseMapper.toResponse(userCourseRepository.save(userCourse));
    }

    /**
     * Cập nhật thời gian hết hạn khóa học (chỉ admin)
     */
    @Transactional
    public UserCourseResponse updateExpiredAt(Long userCourseId, LocalDateTime expiredAt) {
        UserCourse userCourse = userCourseRepository.findById(userCourseId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_COURSE_NOT_EXIST));

        userCourse.setExpiredAt(expiredAt);
        return userCourseMapper.toResponse(userCourseRepository.save(userCourse));
    }

    // ==========================================
    // HÀM DELETE (Xóa)
    // ==========================================

    /**
     * Xóa một đăng ký khóa học (chỉ admin)
     */
    @Transactional
    public void deleteUserCourse(Long userCourseId) {
        UserCourse userCourse = userCourseRepository.findById(userCourseId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_COURSE_NOT_EXIST));
        userCourseRepository.delete(userCourse);
    }

    /**
     * User xóa đăng ký khóa học của chính mình
     */
    @Transactional
    public void deleteUserCourseByUserAndCourse(Long courseId) {
        Long userId = jwtUtils.getCurrentUserId();
        
        UserCourse userCourse = userCourseRepository.findByUserIdAndCourseId(userId, courseId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_COURSE_NOT_EXIST));
        
        userCourseRepository.delete(userCourse);
    }

    /**
     * Xóa toàn bộ đăng ký khóa học của một User (chỉ admin)
     */
    @Transactional
    public void deleteAllUserCoursesByUserId(Long userId) {
        // Kiểm tra User tồn tại
        userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));

        userCourseRepository.deleteByUserId(userId);
    }

    // ==========================================
    // HÀM HELPER & UTILITY
    // ==========================================

    /**
     * Lấy số lượng User đã đăng ký một Course
     */
    public Long getEnrollmentCountByCourse(Long courseId) {
        return userCourseRepository.countByCourseId(courseId);
    }

    /**
     * Lấy số khóa học mà User hiện tại đã đăng ký
     */
    public Long getEnrollmentCountByCurrentUser() {
        Long userId = jwtUtils.getCurrentUserId();
        return userCourseRepository.countByUserId(userId);
    }

    /**
     * Lấy số khóa học mà một User đã đăng ký (chỉ admin)
     */
    public Long getEnrollmentCountByUser(Long userId) {
        return userCourseRepository.countByUserId(userId);
    }
}