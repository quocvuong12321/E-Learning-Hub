package com.king.lms.e_learning_hub.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.king.lms.e_learning_hub.dto.question.QuestionRequest;
import com.king.lms.e_learning_hub.dto.question.QuestionResponse;
import com.king.lms.e_learning_hub.dto.quiz.QuizRequest;
import com.king.lms.e_learning_hub.dto.quiz.QuizResponse;
import com.king.lms.e_learning_hub.dto.quiz.QuizUpdateRequest;
import com.king.lms.e_learning_hub.entity.Lesson;
import com.king.lms.e_learning_hub.entity.Question;
import com.king.lms.e_learning_hub.entity.Quiz;
import com.king.lms.e_learning_hub.exception.AppException;
import com.king.lms.e_learning_hub.exception.ErrorCode;
import com.king.lms.e_learning_hub.mapper.AnswerMapper;
import com.king.lms.e_learning_hub.mapper.QuestionMapper;
import com.king.lms.e_learning_hub.mapper.QuizMapper;
import com.king.lms.e_learning_hub.repository.AnswerRepository;
import com.king.lms.e_learning_hub.repository.LessonRepository;
import com.king.lms.e_learning_hub.repository.QuestionRepository;
import com.king.lms.e_learning_hub.repository.QuizRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuizService {

    QuizRepository quizRepository;
    QuestionRepository questionRepository;
    // AnswerRepository answerRepository;
    LessonRepository lessonRepository;
    QuizMapper quizMapper;
    QuestionMapper questionMapper;
    // AnswerMapper answerMapper;

    @Transactional
    public QuizResponse createFullQuiz(QuizRequest quizRequest) {

        Lesson lesson = lessonRepository.findById(quizRequest.getLessonId())
                .orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_EXIST));

        Quiz quiz = quizMapper.toEntity(quizRequest);
        quiz.setLesson(lesson);


        assignQuizToQuestions(quiz);

        QuizResponse response = quizMapper.toResponse(quizRepository.save(quiz));

        return response;
    }

    /**
     * Hàm helper: Gán Quiz làm cha cho tất cả Question bên trong nó
     */
    private void assignQuizToQuestions(Quiz quiz) {
        if (quiz.getQuestions() != null) {
            quiz.getQuestions().forEach(question -> {
                question.setQuiz(quiz);
                // Với từng question, lại tiếp tục gán nó làm cha cho các answer
                assignQuestionToAnswers(question);
            });
        }
    }

    /**
     * Hàm helper: Gán Question làm cha cho tất cả Answer bên trong nó
     */
    private void assignQuestionToAnswers(Question question) {
        if (question.getAnswers() != null) {
            question.getAnswers().forEach(answer -> answer.setQuestion(question));
        }
    }


    // ==========================================
    // CÁC HÀM GET LẤY CHI TIẾT DỮ LIỆU
    // ==========================================

    /**
     * Lấy chi tiết một Quiz (Kèm theo toàn bộ Question và Answer)
     */
    public QuizResponse getQuizById(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new AppException(ErrorCode.QUIZ_NOT_EXIST));
        return quizMapper.toResponse(quiz);
    }

    /**
     * Lấy toàn bộ danh sách Quizzes của một Lesson
     */
    public List<QuizResponse> getQuizzesByLessonId(Long lessonId) {
        // Lưu ý: Cần tạo thêm method `findByLessonId` trong QuizRepository
        List<Quiz> quizzes = quizRepository.findByLessonId(lessonId);
        return quizzes.stream()
                .map(quizMapper::toResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Lấy chi tiết một Câu hỏi
     */
    public QuestionResponse getQuestionById(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new AppException(ErrorCode.QUESTION_NOT_EXIST));
        return questionMapper.toResponse(question);
    }


    //==========================================
    // CÁC HÀM CRUD CHO QUIZZES, QUESTIONS, ANSWERS
    // ==========================================

    /**
     * Cập nhật thông tin cơ bản của Quiz (Tiêu đề, điểm chuẩn, bài học)
     */
    @Transactional
    public QuizResponse updateQuiz(Long quizId, QuizUpdateRequest request) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new AppException(ErrorCode.QUIZ_NOT_EXIST));

        quiz.setTitle(request.getTitle());
        quiz.setTargetScore(request.getTargetScore());

        // Nếu có thay đổi Lesson
        if (!quiz.getLesson().getId().equals(request.getLessonId())) {
            Lesson lesson = lessonRepository.findById(request.getLessonId())
                    .orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_EXIST));
            quiz.setLesson(lesson);
        }

        // Lưu trước
        quiz = quizRepository.save(quiz);
        
        // Map ra response và ép list question thành null để không trả về
        QuizResponse response = quizMapper.toResponse(quiz);
        response.setQuestions(null);

        return response;
    }

    /**
     * Xóa toàn bộ Quiz (JPA Cascade sẽ tự xóa toàn bộ Question và Answer)
     */
    @Transactional
    public void deleteQuiz(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new AppException(ErrorCode.QUIZ_NOT_EXIST));
        quizRepository.delete(quiz);
    }

    /**
     * Thêm mới một câu hỏi (kèm danh sách câu trả lời) vào một Quiz có sẵn
     */
    @Transactional
    public QuestionResponse addQuestionToQuiz(Long quizId, QuestionRequest request) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new AppException(ErrorCode.QUIZ_NOT_EXIST));

        // MapStruct đã map luôn list Answer bên trong
        Question question = questionMapper.toEntity(request);
        
        // 1. Gán cha Quiz cho Question
        question.setQuiz(quiz);
        
        // 2. Dùng hàm Helper để gán cha Question cho các Answer
        assignQuestionToAnswers(question);

        return questionMapper.toResponse(questionRepository.save(question));
    }

    /**
     * Sửa nội dung câu hỏi và làm mới danh sách câu trả lời
     */
    @Transactional
    public QuestionResponse updateQuestion(Long questionId, QuestionRequest request) {
        Question existingQuestion = questionRepository.findById(questionId)
                .orElseThrow(() -> new AppException(ErrorCode.QUESTION_NOT_EXIST));

        // Cập nhật thông tin của Question
        existingQuestion.setQuestionText(request.getQuestionText());
        existingQuestion.setQuestionType(request.getQuestionType());

        // Thay mới hoàn toàn danh sách Answer cũ bằng danh sách mới
        existingQuestion.getAnswers().clear();

        // Lấy danh sách map từ request mới
        Question incomingQuestion = questionMapper.toEntity(request);
        if (incomingQuestion.getAnswers() != null) {
            existingQuestion.getAnswers().addAll(incomingQuestion.getAnswers());
            // Dùng hàm Helper để gán ngược object Cha cho các Answer vừa được thêm
            assignQuestionToAnswers(existingQuestion);
        }

        return questionMapper.toResponse(questionRepository.save(existingQuestion));
    }

    /**
     * Xóa 1 câu hỏi (Cascade sẽ tự động xóa các câu trả lời thuộc câu hỏi này)
     */
    @Transactional
    public void deleteQuestion(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new AppException(ErrorCode.QUESTION_NOT_EXIST));
        questionRepository.delete(question);
    }

}
