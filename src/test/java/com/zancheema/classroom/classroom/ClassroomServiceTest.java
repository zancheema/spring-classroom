package com.zancheema.classroom.classroom;

import com.zancheema.classroom.classroom.dto.*;
import com.zancheema.classroom.user.User;
import com.zancheema.classroom.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

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
        when(classroomMapper.toClassroomBody(classroomObj))
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
        when(classroomMapper.toClassroomBody(classroomObj))
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
        when(classroomMapper.toClassroomBody(savedClassroom))
                .thenReturn(mockClassroomInfo);


        Optional<ClassroomInfo> optionalClassroomBody = classroomService
                .createClassroom(payload);

        assertThat(optionalClassroomBody).isPresent();
        assertThat(optionalClassroomBody).get().isEqualTo(mockClassroomInfo);
    }

    @Test
    public void addInvalidStudentToClassroomShouldReturnEmpty() {
        ClassroomStudent mockClassroomStudent = new ClassroomStudent(1L, 2L);
        when(classroomRepository.findById(mockClassroomStudent.getClassId()))
                .thenReturn(Optional.empty());

        Optional<ClassroomStudent> optionalClassroomStudent = classroomService
                .addStudentToClassroom(mockClassroomStudent);

        assertThat(optionalClassroomStudent).isEmpty();
    }

    @Test
    public void addStudentToInvalidClassroomShouldReturnEmpty() {
        ClassroomStudent mockClassroomStudent = new ClassroomStudent(1L, 2L);
        when(userRepository.findById(mockClassroomStudent.getStudentId()))
                .thenReturn(Optional.empty());

        Optional<ClassroomStudent> optionalClassroomStudent = classroomService
                .addStudentToClassroom(mockClassroomStudent);

        assertThat(optionalClassroomStudent).isEmpty();
    }

    @Test
    public void addStudentToClassroomSuccessShouldReturnClassroomStudent() {
        ClassroomStudent mockClassroomStudent = new ClassroomStudent(1L, 2L);
        User student = new User();
        student.setId(2L);
        when(userRepository.findById(mockClassroomStudent.getStudentId()))
                .thenReturn(Optional.of(student));
        when(classroomRepository.findById(mockClassroomStudent.getClassId()))
                .thenReturn(Optional.of(classroomObj));
        Classroom savedClassroom = new Classroom();
        savedClassroom.setId(5L);
        when(classroomRepository.save(classroomObj))
                .thenReturn(savedClassroom);

        Optional<ClassroomStudent> optionalClassroomStudent = classroomService
                .addStudentToClassroom(mockClassroomStudent);

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
        when(classroomMapper.toClassroomBody(updatedClassroom))
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
}
