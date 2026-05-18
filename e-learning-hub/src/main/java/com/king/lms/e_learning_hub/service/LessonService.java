package com.king.lms.e_learning_hub.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.king.lms.e_learning_hub.dto.lesson.LessonRequest;
import com.king.lms.e_learning_hub.dto.lesson.LessonResponse;
import com.king.lms.e_learning_hub.entity.Lesson;
import com.king.lms.e_learning_hub.entity.Section;
import com.king.lms.e_learning_hub.mapper.LessonMapper;
import com.king.lms.e_learning_hub.repository.LessonRepository;
import com.king.lms.e_learning_hub.repository.SectionRepository;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class LessonService {

    LessonRepository lessonRepository;
    SectionRepository sectionRepository;
    LessonMapper lessonMapper;

    @Transactional
    public LessonResponse createLesson(LessonRequest request) {
        Section section = sectionRepository.findById(request.getSectionId())
                .orElseThrow(() -> new RuntimeException("Section not found")); // Thay bằng AppException tuỳ ý

        Lesson lesson = lessonMapper.toEntity(request);
        lesson.setSection(section);

        lesson = lessonRepository.save(lesson);
        return lessonMapper.toResponse(lesson);
    }

    public LessonResponse getLessonById(Long id) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));
        return lessonMapper.toResponse(lesson);
    }

    public List<LessonResponse> getLessonsBySectionId(Long sectionId) {
        // Cần thêm method này trong LessonRepository
        List<Lesson> lessons = lessonRepository.findBySectionIdOrderByOrderIndexAsc(sectionId);
        return lessons.stream()
                .map(lessonMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public LessonResponse updateLesson(Long id, LessonRequest request) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));

        // Nếu client muốn đổi bài học sang Section khác
        if (!lesson.getSection().getId().equals(request.getSectionId())) {
            Section section = sectionRepository.findById(request.getSectionId())
                    .orElseThrow(() -> new RuntimeException("Section not found"));
            lesson.setSection(section);
        }

        // Cập nhật các trường
        lesson.setTitle(request.getTitle());
        lesson.setVideoUrl(request.getVideoUrl());
        lesson.setDurationSeconds(request.getDurationSeconds());
        lesson.setContent(request.getContent());
        lesson.setIsPreview(request.getIsPreview());
        lesson.setOrderIndex(request.getOrderIndex());

        lesson = lessonRepository.save(lesson);
        return lessonMapper.toResponse(lesson);
    }

    @Transactional
    public void deleteLesson(Long id) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));
        lessonRepository.delete(lesson);
    }
}