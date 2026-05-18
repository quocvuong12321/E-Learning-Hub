package com.king.lms.e_learning_hub.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.king.lms.e_learning_hub.dto.section.SectionRequest;
import com.king.lms.e_learning_hub.dto.section.SectionResponse;
import com.king.lms.e_learning_hub.entity.Course;
import com.king.lms.e_learning_hub.entity.Section;
import com.king.lms.e_learning_hub.mapper.SectionMapper;
import com.king.lms.e_learning_hub.repository.CourseRepository;
import com.king.lms.e_learning_hub.repository.SectionRepository;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SectionService {
    SectionRepository sectionRepository;
    SectionMapper sectionMapper;
    CourseRepository courseRepository;


    @Transactional
    public SectionResponse createSection(SectionRequest request) {
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found")); // Thay bằng AppException tuỳ ý

        Section section = sectionMapper.toEntity(request);
        section.setCourse(course);

        section = sectionRepository.save(section);
        return sectionMapper.toResponse(section);
    }

    public SectionResponse getSectionById(Long id) {
        Section section = sectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Section not found"));
        return sectionMapper.toResponse(section);
    }

    public List<SectionResponse> getSectionsByCourseId(Long courseId) {
        // Cần thêm method này trong SectionRepository
        List<Section> sections = sectionRepository.findByCourseIdOrderByOrderIndexAsc(courseId);
        return sections.stream()
                .map(sectionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public SectionResponse updateSection(Long id, SectionRequest request) {
        Section section = sectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Section not found"));

        if (!section.getCourse().getId().equals(request.getCourseId())) {
            Course course = courseRepository.findById(request.getCourseId())
                    .orElseThrow(() -> new RuntimeException("Course not found"));
            section.setCourse(course);
        }

        section.setTitle(request.getTitle());
        section.setOrderIndex(request.getOrderIndex());

        section = sectionRepository.save(section);
        return sectionMapper.toResponse(section);
    }

    @Transactional
    public void deleteSection(Long id) {
        Section section = sectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Section not found"));
        sectionRepository.delete(section);
    }
}
