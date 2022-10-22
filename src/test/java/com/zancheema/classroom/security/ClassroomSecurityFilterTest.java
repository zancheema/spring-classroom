package com.zancheema.classroom.security;

import com.zancheema.classroom.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import java.io.IOException;

import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClassroomSecurityFilterTest {
    @Mock
    private SecurityService securityService;

    @Mock
    private FilterChain chain;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private MockHttpServletRequest request;

    private MockHttpServletResponse response;

    private ClassroomSecurityFilter filter;

    @BeforeEach
    public void setup() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filter = new ClassroomSecurityFilter(securityService, securityContext);
    }

    @Test
    public void requestToPathThatIsNotClassroomsPathShouldPassThroughFilter() throws ServletException, IOException {
        request.setContextPath("/auth/login");

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
    }

    @Test
    public void requestDoesNotHaveClassroomIdShouldPass() throws ServletException, IOException {
        request.setContextPath("/api/classrooms");

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
    }

    @Test
    public void requestWithNonLongClassroomIdShouldPass() throws ServletException, IOException {
        request.setContextPath("/api/classrooms/what");

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
    }

    @Test
    public void userIsNeitherStudentNorTeacherOfClassroomShouldReturnForbidden() throws ServletException, IOException {
        when(securityContext.getAuthentication())
                .thenReturn(authentication);
        User user = new User();
        user.setId(1);
        when(authentication.getPrincipal())
                .thenReturn(user);
        long classroomId = 1;

        request.setContextPath("/api/classrooms/" + classroomId);

        filter.doFilterInternal(request, response, chain);

        assertThat(response.getStatus()).isEqualTo(SC_FORBIDDEN);
    }

    @Test
    public void getRequestWhenUserIsStudentShouldPassThroughTheFilter() throws ServletException, IOException {
        when(securityContext.getAuthentication())
                .thenReturn(authentication);
        User user = new User();
        user.setId(1);
        when(authentication.getPrincipal())
                .thenReturn(user);
        long classroomId = 1;

        request.setContextPath("/api/classrooms/" + classroomId);
        request.setMethod(RequestMethod.GET.name());
        when(securityService.classHasTheStudent(classroomId, user.getId()))
                .thenReturn(true);

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
    }

    @Test
    public void anyRequestMethodOtherThanGetWhenUserIsStudentShouldReturnForbidden() throws ServletException, IOException {
        when(securityContext.getAuthentication())
                .thenReturn(authentication);
        User user = new User();
        user.setId(1);
        when(authentication.getPrincipal())
                .thenReturn(user);
        long classroomId = 1;

        request.setContextPath("/api/classrooms/" + classroomId + "/add/student");
        request.setMethod(RequestMethod.POST.name());
        when(securityService.classHasTheStudent(classroomId, user.getId()))
                .thenReturn(true);

        filter.doFilterInternal(request, response, chain);

        assertThat(response.getStatus()).isEqualTo(SC_FORBIDDEN);
    }

    @Test
    public void getRequestWhenUserIsTeacherShouldPassThroughTheFilter() throws ServletException, IOException {
        when(securityContext.getAuthentication())
                .thenReturn(authentication);
        User user = new User();
        user.setId(1);
        when(authentication.getPrincipal())
                .thenReturn(user);
        long classroomId = 1;

        request.setContextPath("/api/classrooms/" + classroomId);
        request.setMethod(RequestMethod.GET.name());
        when(securityService.classHasTheTeacher(classroomId, user.getId()))
                .thenReturn(true);

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
    }

    @Test
    public void getRequestOtherThanGetWhenUserIsTeacherShouldPassThroughTheFilter() throws ServletException, IOException {
        when(securityContext.getAuthentication())
                .thenReturn(authentication);
        User user = new User();
        user.setId(1);
        when(authentication.getPrincipal())
                .thenReturn(user);
        long classroomId = 1;

        request.setContextPath("/api/classrooms/" + classroomId + "/add/student");
        request.setMethod(RequestMethod.POST.name());
        when(securityService.classHasTheTeacher(classroomId, user.getId()))
                .thenReturn(true);

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
    }
}