package com.zancheema.classroom.security;

import com.zancheema.classroom.user.User;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ClassroomTeacherFilter extends OncePerRequestFilter {
    public static final String API_CLASSROOMS = "/api/classrooms/";

    private final SecurityService securityService;

    private final SecurityContext securityContext;

    public ClassroomTeacherFilter(SecurityService securityService, SecurityContext securityContext) {
        this.securityService = securityService;
        this.securityContext = securityContext;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getContextPath().startsWith(API_CLASSROOMS)) {
            try {
                String classroomIdStr = request.getContextPath()
                        .substring(API_CLASSROOMS.length())
                        .split("/")[0];
                long classroomId = Long.parseLong(classroomIdStr);

                User user = (User) securityContext.getAuthentication().getPrincipal();
                boolean isTeacher = securityService.classHasTheTeacher(classroomId, user.getId());

                if (isTeacher) {
                    filterChain.doFilter(request, response);
                } else {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                }
            } catch (Exception e) {
                doFilter(request, response, filterChain);
            }
        } else {
            doFilter(request, response, filterChain);
        }
    }
}
