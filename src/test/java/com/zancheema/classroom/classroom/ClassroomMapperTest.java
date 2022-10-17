package com.zancheema.classroom.classroom;

import com.zancheema.classroom.classroom.dto.ClassroomInfo;
import com.zancheema.classroom.classroom.dto.ClassroomStudents;
import com.zancheema.classroom.classroom.dto.Student;
import com.zancheema.classroom.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ClassroomMapperTest {
    public ClassroomMapper classroomMapper;

    @BeforeEach
    public void setup() {
        classroomMapper = new ClassroomMapper();
    }

    @Test
    public void toClassroomBodyForClassroomShouldReturnClassroomBodyObject() {
        Classroom classroom = new Classroom();
        classroom.setId(1L);
        classroom.setTitle("title");
        classroom.setSubject("sub");

        User teacher = new User();
        teacher.setId(2L);
        teacher.setEmail("teacher@classroom.com");
        teacher.setPassword("pass");
        teacher.setFirstName("john");
        teacher.setLastName("doe");
        classroom.setTeacher(teacher);

        ClassroomInfo classroomInfo = classroomMapper.toClassroomBody(classroom);

        assertThat(classroomInfo).isNotNull();
        assertThat(classroomInfo.id()).isEqualTo(classroom.getId());
        assertThat(classroomInfo.title()).isEqualTo(classroom.getTitle());
        assertThat(classroomInfo.subject()).isEqualTo(classroom.getSubject());
        assertThat(classroomInfo.teacher().id()).isEqualTo(classroom.getTeacher().getId());
        assertThat(classroomInfo.teacher().firstName()).isEqualTo(classroom.getTeacher().getFirstName());
        assertThat(classroomInfo.teacher().lastName()).isEqualTo(classroom.getTeacher().getLastName());
    }

    @Test
    public void classroomStudentsForClassroomShouldReturnClassroomStudentsObject() {
        Classroom classroom = new Classroom();
        classroom.setId(1L);
        User student1 = new User();
        student1.setId(2L);
        student1.setFirstName("john");
        student1.setLastName("doe");
        student1.setEmail("john@mail.com");
        User student2 = new User();
        student2.setId(3L);
        student2.setFirstName("jane");
        student2.setLastName("doe");
        student2.setEmail("jane@example.com");
        classroom.setStudents(Set.of(student1, student2));

        ClassroomStudents classroomStudents = classroomMapper.getClassroomStudents(classroom);

        assertThat(classroomStudents).isNotNull();
        assertThat(classroom.getId()).isEqualTo(classroomStudents.classroomId());
        assertThat(classroomStudents.students().size())
                .isEqualTo(classroom.getStudents().size());

        int size = classroom.getStudents().size();
        User[] givenStudents = classroom.getStudents().toArray(new User[size]);
        Student[] returnedStudents = classroomStudents.students().toArray(new Student[size]);
        for (int i = 0; i < size; i++) {
            User givenStudent = givenStudents[i];
            Student returnedStudent = returnedStudents[i];

            assertThat(returnedStudent.id()).isEqualTo(givenStudent.getId());
            assertThat(returnedStudent.firstName()).isEqualTo(givenStudent.getFirstName());
            assertThat(returnedStudent.lastName()).isEqualTo(givenStudent.getLastName());
            assertThat(returnedStudent.email()).isEqualTo(givenStudent.getEmail());
        }
    }

    @Test
    public void toClassroomShouldReturnClassroomObjectWithProvidedArguments() {
        String title = "title";
        String subject = "sub";

        Classroom classroom = classroomMapper.toClassroom(title, subject);

        assertThat(classroom).isNotNull();
        assertThat(classroom.getTitle()).isEqualTo(title);
        assertThat(classroom.getSubject()).isEqualTo(subject);
    }
}