package com.zancheema.classroom.security;

import com.zancheema.classroom.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SecurityService securityService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic(withDefaults())
                .addFilterAfter(classroomSecurityFilter(), BasicAuthenticationFilter.class)
                .authorizeHttpRequests(authz -> authz
                        .antMatchers("/auth/**").permitAll()
                        .antMatchers(HttpMethod.GET, "/api/**").hasAuthority("read")
                        .antMatchers("/api/**").hasAuthority("write")
                        .antMatchers("/api/**").authenticated()
                        .anyRequest().denyAll()
                );
        return http.build();
    }

    @Bean
    public ClassroomSecurityFilter classroomSecurityFilter() {
        return new ClassroomSecurityFilter(securityService, securityContext());
    }

    @Bean
    public SecurityContext securityContext() {
        return SecurityContextHolder.getContext();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Username " + username + " not found"
                ));
    }
}
