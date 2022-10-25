package com.zancheema.classroom.classroom;

import com.zancheema.classroom.classroom.dto.*;
import com.zancheema.classroom.quiz.Quiz;
import com.zancheema.classroom.quiz.QuizMapper;
import com.zancheema.classroom.quiz.dto.QuizInfo;
import com.zancheema.classroom.user.User;
import com.zancheema.classroom.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ClassroomServiceTest {
    @Mock
    private ClassroomRepository classroomRepository;

    @Mock
    private ClassroomMapper classroomMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private QuizMapper quizMapper;

    @InjectMocks
    private ClassroomServiceImpl classroomService;

    private Classroom classroomObj;

    @BeforeEach
    public void setup() {
        classroomObj = new Classroom();
        classroomObj.setId(1L);
        classroomObj.setTitle("title");
        classroomObj.setSubject("sub");

        User teacher = new User();
        teacher.setId(2L);
        teacher.setFirstName("jane");
        teacher.setLastName("doe");
        teacher.setEmail("jane@example.com");
        teacher.setPassword("password");
        classroomObj.setTeacher(teacher);

        classroomObj.setStudents(new HashSet<>());
    }

    @Test
    public void findByIdIfNotExistShouldReturnEmpty() {
        when(classroomRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThat(classroomService.findClassroomById(1L)).isEmpty();
    }

    @Test
    public void findByIdExistsShouldReturnClassroomObject() {
        when(classroomRepository.findById(1L))
                .thenReturn(Optional.of(classroomObj));
        ClassroomInfo mockClassroomInfo = new ClassroomInfo(3L, null, null, null);
        when(classroomMapper.toClassroomInfo(classroomObj))
                .thenReturn(mockClassroomInfo);

        Optional<ClassroomInfo> optionalClassroomBody = classroomService.findClassroomById(1L);

        assertThat(optionalClassroomBody).isPresent();
        ClassroomInfo classroomInfo = optionalClassroomBody.get();
        assertThat(classroomInfo).isEqualTo(mockClassroomInfo);
    }

    @Test
    public void findTeacherWhenClassroomDoesNotExistShouldReturnEmpty() {
        when(classroomRepository.findById(1L))
                .thenReturn(Optional.empty());

        Optional<Teacher> teacher = classroomService.findTeacher(1L);

        assertThat(teacher).isEmpty();
    }

    @Test
    public void findTeacherShouldReturnTeacherObject() {
        when(classroomRepository.findById(1L))
                .thenReturn(Optional.of(classroomObj));
        ClassroomInfo mockClassroomInfo = new ClassroomInfo(
                3L, new Teacher(4L, "first", "last"), null, null
        );
        when(classroomMapper.toClassroomInfo(classroomObj))
                .thenReturn(mockClassroomInfo);

        Optional<Teacher> optionalTeacher = classroomService.findTeacher(1L);

        assertThat(optionalTeacher).isPresent();
        Teacher teacher = optionalTeacher.get();
        assertThat(teacher).isEqualTo(mockClassroomInfo.teacher());
    }

    @Test
    public void findClassroomStudentsWhenClassroomDoesNotExistShouldReturnEmpty() {
        when(classroomRepository.findById(1L))
                .thenReturn(Optional.empty());

        Optional<ClassroomStudents> optionalClassroomStudents = classroomService.findClassroomStudents(1L);

        assertThat(optionalClassroomStudents).isEmpty();
    }

    @Test
    public void findClassroomStudentShouldReturnClassroomStudentsObject() {
        when(classroomRepository.findById(1L))
                .thenReturn(Optional.of(classroomObj));
        ClassroomStudents mockClassroomStudents = new ClassroomStudents(1L, List.of(
                new Student(2L, "first", "last", "first@mail.com")
        ));
        when(classroomMapper.getClassroomStudents(classroomObj))
                .thenReturn(mockClassroomStudents);

        Optional<ClassroomStudents> optionalClassroomStudents = classroomService.findClassroomStudents(1L);

        assertThat(optionalClassroomStudents).isPresent();
        ClassroomStudents classroomStudents = optionalClassroomStudents.get();
        assertThat(classroomStudents).isEqualTo(mockClassroomStudents);
    }

    @Test
    public void createClassroomFailureShouldReturnEmpty() {
        when(userRepository.findById(2L))
                .thenReturn(Optional.empty());

        Optional<ClassroomInfo> optionalClassroomBody = classroomService
                .createClassroom(new ClassroomCreationPayload(2L, "t", "s"));

        assertThat(optionalClassroomBody).isEmpty();
    }

    @Test
    public void createClassroomSuccessShouldReturnClassroomBodyObject() {
        when(userRepository.findById(2L))
                .thenReturn(Optional.of(classroomObj.getTeacher()));
        ClassroomCreationPayload payload = new ClassroomCreationPayload(2L, "t", "s");
        when(classroomMapper.toClassroom(payload.getTitle(), payload.getSubject()))
                .thenReturn(classroomObj);
        Classroom savedClassroom = new Classroom();
        savedClassroom.setId(5L);
        when(classroomRepository.save(classroomObj))
                .thenReturn(savedClassroom);
        ClassroomInfo mockClassroomInfo = new ClassroomInfo(
                3L, new Teacher(4L, "first", "last"), null, null
        );
        when(classroomMapper.toClassroomInfo(savedClassroom))
                .thenReturn(mockClassroomInfo);


        Optional<ClassroomInfo> optionalClassroomBody = classroomService
                .createClassroom(payload);

        assertThat(optionalClassroomBody).isPresent();
        assertThat(optionalClassroomBody).get().isEqualTo(mockClassroomInfo);
    }

    @Test
    public void addInvalidStudentToClassroomShouldReturnEmpty() {
        long classroomId = 1L;
        ClassroomStudent mockClassroomStudent = new ClassroomStudent(2L);
        when(classroomRepository.findById(classroomId))
                .thenReturn(Optional.empty());

        Optional<ClassroomStudent> optionalClassroomStudent = classroomService
                .addStudentToClassroom(classroomId, mockClassroomStudent);

        assertThat(optionalClassroomStudent).isEmpty();
    }

    @Test
    public void addStudentToInvalidClassroomShouldReturnEmpty() {
        long classroomId = 1L;
        ClassroomStudent mockClassroomStudent = new ClassroomStudent(2L);
        when(userRepository.findById(mockClassroomStudent.getStudentId()))
                .thenReturn(Optional.empty());

        Optional<ClassroomStudent> optionalClassroomStudent = classroomService
                .addStudentToClassroom(classroomId, mockClassroomStudent);

        assertThat(optionalClassroomStudent).isEmpty();
    }

    @Test
    public void addStudentToClassroomSuccessShouldReturnClassroomStudent() {
        long classroomId = 1L;
        ClassroomStudent mockClassroomStudent = new ClassroomStudent(2L);
        User student = new User();
        student.setId(2L);
        when(userRepository.findById(mockClassroomStudent.getStudentId()))
                .thenReturn(Optional.of(student));
        when(classroomRepository.findById(classroomId))
                .thenReturn(Optional.of(classroomObj));
        Classroom savedClassroom = new Classroom();
        savedClassroom.setId(5L);
        when(classroomRepository.save(classroomObj))
                .thenReturn(savedClassroom);

        Optional<ClassroomStudent> optionalClassroomStudent = classroomService
                .addStudentToClassroom(classroomId, mockClassroomStudent);

        assertThat(optionalClassroomStudent).isPresent();
        assertThat(classroomObj.getStudents()).contains(student);
        assertThat(optionalClassroomStudent).get().isEqualTo(mockClassroomStudent);
    }

    @Test
    public void updateClassroomWithInvalidClassroomIdShouldReturnEmpty() {
        when(classroomRepository.findById(1L))
                .thenReturn(Optional.empty());

        Optional<ClassroomInfo> optionalClassroomBody = classroomService
                .updateClassroom(1L, new UpdateClassroomPayload(2L, "title", "sub"));

        assertThat(optionalClassroomBody).isEmpty();
    }

    @Test
    public void updateClassroomWithInvalidTeacherIdShouldReturnEmpty() {
        when(classroomRepository.findById(1L))
                .thenReturn(Optional.of(classroomObj));
        when(userRepository.findById(2L))
                .thenReturn(Optional.empty());

        Optional<ClassroomInfo> optionalClassroomBody = classroomService
                .updateClassroom(1L, new UpdateClassroomPayload(2L, "title", "sub"));

        assertThat(optionalClassroomBody).isEmpty();
    }

    @Test
    public void updateClassroomSuccessShouldReturnUpdatedClassroomBodyObject() {
        UpdateClassroomPayload payload = new UpdateClassroomPayload(2L, "title", "sub");
        when(classroomRepository.findById(1L))
                .thenReturn(Optional.of(classroomObj));
        User mockTeacher = new User();
        mockTeacher.setId(2L);
        when(userRepository.findById(payload.getTeacherId()))
                .thenReturn(Optional.of(mockTeacher));
        Classroom updatedClassroom = new Classroom();
        updatedClassroom.setId(5L);
        when(classroomRepository.save(classroomObj))
                .thenReturn(updatedClassroom);
        ClassroomInfo updatedClassroomInfo = new ClassroomInfo(6L, null, "", "");
        when(classroomMapper.toClassroomInfo(updatedClassroom))
                .thenReturn(updatedClassroomInfo);

        Optional<ClassroomInfo> optionalClassroomBody = classroomService
                .updateClassroom(1L, payload);

        assertThat(optionalClassroomBody).isPresent();
        ClassroomInfo classroomInfo = optionalClassroomBody.get();
        // the returned object is updated object
        assertThat(classroomInfo).isEqualTo(updatedClassroomInfo);
        // check behavior: the fields of db object were updated
        assertThat(classroomObj.getTeacher()).isEqualTo(mockTeacher);
        assertThat(classroomObj.getTitle()).isEqualTo(payload.getTitle());
        assertThat(classroomObj.getSubject()).isEqualTo(payload.getSubject());
    }

    @Test
    public void getAttendingClassroomsWithInvalidUsernameShouldReturnEmpty() {
        when(userRepository.findByEmail("user"))
                .thenReturn(Optional.empty());

        Optional<AttendingClassrooms> attendingClassrooms = classroomService
                .findAttendingClassrooms("user");

        assertThat(attendingClassrooms).isEmpty();
    }

    @Test
    public void getAttendingClassroomsSuccessShouldReturnAttendingClassroomsObject() {
        User user = new User();
        user.setId(1);
        user.setEmail("user");
        when(userRepository.findByEmail("user"))
                .thenReturn(Optional.of(user));
        List<Classroom> classrooms = List.of(
                new Classroom(2, user, Set.of(user), "title", "sub"),
                new Classroom(3, user, Set.of(user), "title2", "sub2")
        );
        when(classroomRepository.findByStudentsId(user.getId()))
                .thenReturn(classrooms);
        ClassroomInfo info1 = new ClassroomInfo(1, null, null, null);
        when(classroomMapper.toClassroomInfo(classrooms.get(0)))
                .thenReturn(info1);
        ClassroomInfo info2 = new ClassroomInfo(2, null, null, null);
        when(classroomMapper.toClassroomInfo(classrooms.get(1)))
                .thenReturn(info2);

        Optional<AttendingClassrooms> optionalAttendingClassrooms = classroomService
                .findAttendingClassrooms(user.getEmail());

        assertThat(optionalAttendingClassrooms).isPresent();
        AttendingClassrooms attendingClassrooms = optionalAttendingClassrooms.get();
        assertThat(attendingClassrooms.studentId()).isEqualTo(user.getId());
        assertThat(attendingClassrooms.attendingClassrooms()).size().isEqualTo(classrooms.size());
        assertThat(attendingClassrooms.attendingClassrooms()).contains(info1, info2);
    }

    @Test
    public void findClassroomQuizzesInfoWhenClassroomIdAndQuizIdCombinationIsInvalidShouldReturnEmpty() {
        when(classroomRepository.findById(1L))
                .thenReturn(Optional.empty());

        Optional<ClassroomQuizzesInfo> info = classroomService.findClassroomQuizzesInfo(1L);

        assertThat(info).isEmpty();
    }

    @Test
    public void findClassroomQuizzesInfoSuccessShouldReturnClassroomQuizzesInfoObject() {
        Quiz quiz1 = new Quiz(2, null, LocalDateTime.now(), LocalTime.now());
        Quiz quiz2 = new Quiz(2, null, LocalDateTime.now(), LocalTime.now());
        Classroom classroom = new Classroom(
                1, null, null, null, null, Set.of(quiz1, quiz2));
        when(classroomRepository.findById(1L))
                .thenReturn(Optional.of(classroom));
        ClassroomQuizzesInfo mockInfo = new ClassroomQuizzesInfo(3, Set.of());
        when(classroomMapper.toClassroomQuizzesInfo(classroom))
                .thenReturn(mockInfo);

        Optional<ClassroomQuizzesInfo> optionalInfo = classroomService.findClassroomQuizzesInfo(1);

        assertThat(optionalInfo).isPresent();
        ClassroomQuizzesInfo info = optionalInfo.get();
        assertThat(info).isEqualTo(mockInfo);
    }

    @Test
    public void findQuizInfoWhenClassroomIdDoesNotExistShouldReturnEmpty() {
        Optional<QuizInfo> optionalQuizInfo = classroomService
                .findQuizInfo(1, 2);

        assertThat(optionalQuizInfo).isEmpty();
    }

    @Test
    public void findQuizInfoWhenQuizIdDoesNotExistShouldReturnEmpty() {
        long classroomId = 1;
        long quizInfoId = 3;
        when(classroomRepository.findById(classroomId))
                .thenReturn(Optional.of(classroomObj));
        Quiz quiz1 = new Quiz(2, classroomObj, null, null);
        Quiz quiz2 = new Quiz(quizInfoId, classroomObj, null, null);
        // do not include quiz2 in classroom quizzes
        classroomObj.setQuizzes(Set.of(quiz1));

        Optional<QuizInfo> optionalQuizInfo = classroomService
                .findQuizInfo(classroomId, quizInfoId);

        assertThat(optionalQuizInfo).isEmpty();
    }

    @Test
    public void findQuizInfoByClassroomIdSuccessShouldReturnQuizInfoObject() {
        long classroomId = 1;
        long quizInfoId = 3;
        when(classroomRepository.findById(classroomId))
                .thenReturn(Optional.of(classroomObj));
        Quiz quiz1 = new Quiz(2, classroomObj, null, null);
        Quiz quiz2 = new Quiz(quizInfoId, classroomObj, null, null);
        classroomObj.setQuizzes(Set.of(quiz1, quiz2));
        QuizInfo quizInfo = new QuizInfo(1, 2, null, null, null, null);
        when(quizMapper.toQuizInfo(quiz2))
                .thenReturn(quizInfo);

        Optional<QuizInfo> optionalQuizInfo = classroomService
                .findQuizInfo(classroomId, quizInfoId);

        assertThat(optionalQuizInfo)
                .isPresent()
                .get().isEqualTo(quizInfo);
    }
}
