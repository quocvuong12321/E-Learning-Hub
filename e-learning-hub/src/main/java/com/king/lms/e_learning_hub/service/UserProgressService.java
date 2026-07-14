package com.king.lms.e_learning_hub.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.king.lms.e_learning_hub.dto.user_progress.UserProgressRequest;
import com.king.lms.e_learning_hub.dto.user_progress.UserProgressResponse;
import com.king.lms.e_learning_hub.dto.user_progress.UserProgressUpdateRequest;
import com.king.lms.e_learning_hub.entity.Lesson;
import com.king.lms.e_learning_hub.entity.User;
import com.king.lms.e_learning_hub.entity.UserProgress;
import com.king.lms.e_learning_hub.exception.AppException;
import com.king.lms.e_learning_hub.exception.ErrorCode;
import com.king.lms.e_learning_hub.mapper.UserProgressMapper;
import com.king.lms.e_learning_hub.repository.LessonRepository;
import com.king.lms.e_learning_hub.repository.UserProgressRepository;
import com.king.lms.e_learning_hub.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserProgressService {

    UserProgressRepository userProgressRepository;
    UserRepository userRepository;
    LessonRepository lessonRepository;
    UserProgressMapper userProgressMapper;

    // ==========================================
    // HÀM CREATE (Tạo mới)
    // ==========================================

    /**
     * Tạo mới tiến độ học tập cho user
     */
    @Transactional
    public UserProgressResponse createUserProgress(UserProgressRequest request) {
        
        // Kiểm tra User tồn tại
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));

        // Kiểm tra Lesson tồn tại
        Lesson lesson = lessonRepository.findById(request.getLessonId())
                .orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_EXIST));

        // Kiểm tra user chưa có tiến độ cho lesson này
        userProgressRepository.findByUserIdAndLessonId(user.getId(), lesson.getId())
                .ifPresent(up -> {
                    throw new AppException(ErrorCode.USER_PROGRESS_ALREADY_EXIST);
                });

        // Map từ request sang entity
        UserProgress userProgress = userProgressMapper.toEntity(request);
        userProgress.setUser(user);
        userProgress.setLesson(lesson);
        
        // Gán giá trị mặc định nếu không có
        if (userProgress.getIsCompleted() == null) {
            userProgress.setIsCompleted(false);
        }
        if (userProgress.getLastWatchedTime() == null) {
            userProgress.setLastWatchedTime(0);
        }

        // Lưu và trả về response
        return userProgressMapper.toResponse(userProgressRepository.save(userProgress));
    }

    // ==========================================
    // HÀM GET (Lấy chi tiết / danh sách)
    // ==========================================

    /**
     * Lấy chi tiết tiến độ học tập
     */
    public UserProgressResponse getUserProgressById(Long userProgressId) {
        UserProgress userProgress = userProgressRepository.findById(userProgressId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_PROGRESS_NOT_EXIST));
        return userProgressMapper.toResponse(userProgress);
    }

    /**
     * Lấy toàn bộ tiến độ học tập của một user
     */
    public List<UserProgressResponse> getProgressByUserId(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));

        List<UserProgress> progresses = userProgressRepository.findByUserId(userId);
        return progresses.stream()
                .map(userProgressMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Lấy toàn bộ tiến độ của một lesson
     */
    public List<UserProgressResponse> getProgressByLessonId(Long lessonId) {
        lessonRepository.findById(lessonId)
                .orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_EXIST));

        List<UserProgress> progresses = userProgressRepository.findByLessonId(lessonId);
        return progresses.stream()
                .map(userProgressMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Lấy tiến độ của user trong một lesson
     */
    public UserProgressResponse getProgressByUserAndLesson(Long userId, Long lessonId) {
        UserProgress userProgress = userProgressRepository.findByUserIdAndLessonId(userId, lessonId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_PROGRESS_NOT_EXIST));
        return userProgressMapper.toResponse(userProgress);
    }

    /**
     * Lấy số lesson hoàn thành của user
     */
    public Long getCompletedLessonCountByUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));
        return userProgressRepository.countByUserIdAndIsCompletedTrue(userId);
    }

    /**
     * Lấy số user hoàn thành lesson
     */
    public Long getCompletedUserCountByLesson(Long lessonId) {
        lessonRepository.findById(lessonId)
                .orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_EXIST));
        return userProgressRepository.countByLessonIdAndIsCompletedTrue(lessonId);
    }

    // ==========================================
    // HÀM UPDATE (Cập nhật)
    // ==========================================

    /**
     * Cập nhật tiến độ xem và trạng thái hoàn thành
     */
    @Transactional
    public UserProgressResponse updateUserProgress(Long userProgressId, UserProgressUpdateRequest request) {
        UserProgress userProgress = userProgressRepository.findById(userProgressId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_PROGRESS_NOT_EXIST));

        if (request.getLastWatchedTime() != null) {
            userProgress.setLastWatchedTime(request.getLastWatchedTime());
        }
        if (request.getIsCompleted() != null) {
            userProgress.setIsCompleted(request.getIsCompleted());
        }

        return userProgressMapper.toResponse(userProgressRepository.save(userProgress));
    }

    /**
     * Cập nhật thời gian xem bài học
     */
    @Transactional
    public UserProgressResponse updateLastWatchedTime(Long userProgressId, Integer watchedTime) {
        UserProgress userProgress = userProgressRepository.findById(userProgressId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_PROGRESS_NOT_EXIST));

        userProgress.setLastWatchedTime(watchedTime);
        return userProgressMapper.toResponse(userProgressRepository.save(userProgress));
    }

    /**
     * Đánh dấu bài học đã hoàn thành
     */
    @Transactional
    public UserProgressResponse completeLesson(Long userProgressId) {
        UserProgress userProgress = userProgressRepository.findById(userProgressId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_PROGRESS_NOT_EXIST));

        userProgress.setIsCompleted(true);
        return userProgressMapper.toResponse(userProgressRepository.save(userProgress));
    }

    // ==========================================
    // HÀM DELETE (Xóa)
    // ==========================================

    /**
     * Xóa tiến độ học tập
     */
    @Transactional
    public void deleteUserProgress(Long userProgressId) {
        UserProgress userProgress = userProgressRepository.findById(userProgressId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_PROGRESS_NOT_EXIST));
        userProgressRepository.delete(userProgress);
    }

    /**
     * Xóa toàn bộ tiến độ của user
     */
    @Transactional
    public void deleteAllProgressByUserId(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));

        userProgressRepository.deleteByUserId(userId);
    }
}