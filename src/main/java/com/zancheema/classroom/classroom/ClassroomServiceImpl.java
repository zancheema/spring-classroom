package com.zancheema.classroom.classroom;

import com.zancheema.classroom.answer.Answer;
import com.zancheema.classroom.answer.AnswerRepository;
import com.zancheema.classroom.classroom.dto.*;
import com.zancheema.classroom.question.Question;
import com.zancheema.classroom.question.QuestionRepository;
import com.zancheema.classroom.quiz.Quiz;
import com.zancheema.classroom.quiz.QuizMapper;
import com.zancheema.classroom.quiz.QuizRepository;
import com.zancheema.classroom.quiz.dto.QuizInfo;
import com.zancheema.classroom.solution.Solution;
import com.zancheema.classroom.solution.SolutionRepository;
import com.zancheema.classroom.user.User;
import com.zancheema.classroom.user.UserRepository;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ClassroomServiceImpl implements ClassroomService {
    private final ClassroomRepository classroomRepository;
    private final ClassroomMapper classroomMapper;
    private final UserRepository userRepository;
    private final QuizMapper quizMapper;
    private final QuizRepository quizRepository;
    private final SolutionRepository solutionRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    public ClassroomServiceImpl(ClassroomRepository classroomRepository, ClassroomMapper classroomMapper, UserRepository userRepository, QuizMapper quizMapper, QuizRepository quizRepository, SolutionRepository solutionRepository, QuestionRepository questionRepository, AnswerRepository answerRepository) {
        this.classroomRepository = classroomRepository;
        this.classroomMapper = classroomMapper;
        this.userRepository = userRepository;
        this.quizMapper = quizMapper;
        this.quizRepository = quizRepository;
        this.solutionRepository = solutionRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
    }

    @Override
    public Optional<ClassroomInfo> findClassroomById(long classroomId) {
        return classroomRepository.findById(classroomId)
                .map(classroomMapper::toClassroomInfo);
    }

    @Override
    public Optional<Teacher> findTeacher(long classroomId) {
        return this.findClassroomById(classroomId)
                .map(ClassroomInfo::teacher);
    }

    @Override
    public Optional<ClassroomStudents> findClassroomStudents(long classroomId) {
        return classroomRepository.findById(classroomId)
                .map(classroomMapper::getClassroomStudents);
    }

    @Override
    public Optional<ClassroomInfo> createClassroom(ClassroomCreationPayload payload) {
        Optional<User> teacher = userRepository.findById(payload.getTeacherId());
        if (teacher.isEmpty()) return Optional.empty();

        Classroom classroom = classroomMapper.toClassroom(payload.getTitle(), payload.getSubject());
        classroom.setTeacher(teacher.get());

        Classroom savedClassroom = classroomRepository.save(classroom);

        ClassroomInfo classroomInfo = classroomMapper.toClassroomInfo(savedClassroom);

        return Optional.of(classroomInfo);
    }

    @Override
    public Optional<ClassroomStudent> addStudentToClassroom(long classroomId, ClassroomStudent payload) {
        Optional<Classroom> optionalClassroom = classroomRepository.findById(classroomId);
        Optional<User> optionalStudent = userRepository.findById(payload.getStudentId());

        if (optionalClassroom.isEmpty() || optionalStudent.isEmpty()) {
            return Optional.empty();
        }

        Classroom classroom = optionalClassroom.get();
        classroom.getStudents().add(optionalStudent.get());
        classroomRepository.save(classroom);

        return Optional.of(payload);
    }

    @Override
    public Optional<ClassroomInfo> updateClassroom(long classroomId, UpdateClassroomPayload payload) {
        Optional<Classroom> optionalClassroom = classroomRepository.findById(classroomId);
        Optional<User> optionalTeacher = userRepository.findById(payload.getTeacherId());

        if (optionalClassroom.isEmpty() || optionalTeacher.isEmpty()) {
            return Optional.empty();
        }

        Classroom classroom = optionalClassroom.get();
        classroom.setTeacher(optionalTeacher.get());
        classroom.setTitle(payload.getTitle());
        classroom.setSubject(payload.getSubject());

        Classroom updatedClassroom = classroomRepository.save(classroom);

        ClassroomInfo classroomInfo = classroomMapper.toClassroomInfo(updatedClassroom);
        return Optional.of(classroomInfo);
    }

    @Override
    public Optional<AttendingClassrooms> findAttendingClassrooms(String username) {
        Optional<User> optionalUser = userRepository.findByEmail(username);
        if (optionalUser.isEmpty()) return Optional.empty();

        User user = optionalUser.get();
        Set<ClassroomInfo> classrooms = classroomRepository.findByStudentsId(user.getId())
                .stream()
                .map(classroomMapper::toClassroomInfo)
                .collect(Collectors.toSet());

        return Optional.of(new AttendingClassrooms(user.getId(), classrooms));
    }

    @Override
    public Optional<ClassroomQuizzesInfo> findClassroomQuizzesInfo(long classroomId) {
        Optional<Classroom> optionalClassroom = classroomRepository.findById(classroomId);
        if (optionalClassroom.isEmpty()) return Optional.empty();

        Classroom classroom = optionalClassroom.get();
        ClassroomQuizzesInfo info = classroomMapper.toClassroomQuizzesInfo(classroom);
        return Optional.of(info);
    }

    @Override
    public Optional<QuizInfo> findQuizInfo(long classroomId, long quizId) {
        Optional<Classroom> optionalClassroom = classroomRepository.findById(classroomId);
        if (optionalClassroom.isEmpty()) return Optional.empty();

        Classroom classroom = optionalClassroom.get();
        return classroom.getQuizzes()
                .stream()
                .filter(quiz -> quizId == quiz.getId())
                .map(quizMapper::toQuizInfo)
                .findFirst();
    }

    @Transactional
    @Override
    public boolean submitQuiz(long classroomId, QuizSubmissionPayload payload) {
        Optional<Quiz> optionalQuiz = quizRepository
                .findByIdAndClassroomId(payload.getQuizId(), classroomId);
        Optional<User> optionalStudent = userRepository.findById(payload.getStudentId());
        if (optionalQuiz.isEmpty() || optionalStudent.isEmpty()) return false;


        Solution solution = new Solution(optionalQuiz.get(), optionalStudent.get());
        solution = solutionRepository.save(solution);
        for (var submittedAnswer : payload.getAnswers()) {
            Optional<Question> optionalQuestion = questionRepository.findById(submittedAnswer.getQuestionId());
            if (optionalQuestion.isEmpty()) return false;
            Answer answer = new Answer(optionalQuestion.get(), solution, submittedAnswer.getValue());
            answerRepository.save(answer);
        }
        return true;
    }
}
