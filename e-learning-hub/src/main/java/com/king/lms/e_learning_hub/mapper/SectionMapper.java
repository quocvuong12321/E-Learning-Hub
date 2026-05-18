package com.king.lms.e_learning_hub.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.king.lms.e_learning_hub.dto.section.SectionRequest;
import com.king.lms.e_learning_hub.dto.section.SectionResponse;
import com.king.lms.e_learning_hub.entity.Section;

// Khai báo uses = {LessonMapper.class} để tự động map List<Lesson> sang List<LessonResponse>
@Mapper(componentModel = "spring", uses = {LessonMapper.class})
public interface SectionMapper {

    @Mapping(source = "course.id", target = "courseId")
    SectionResponse toResponse(Section section);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "course", ignore = true) // Course object sẽ được set ở Service
    Section toEntity(SectionRequest request);
}