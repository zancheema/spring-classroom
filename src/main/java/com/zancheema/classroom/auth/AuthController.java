package com.zancheema.classroom.auth;

import com.zancheema.classroom.auth.dto.CreatedUser;
import com.zancheema.classroom.auth.dto.SignupPayload;
import com.zancheema.classroom.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<CreatedUser> signup(@RequestBody @Valid SignupPayload payload) throws URISyntaxException {
        Optional<CreatedUser> user = userService.createUser(payload);
        if (user.isEmpty()) return ResponseEntity.badRequest().build();

        URI uri = new URI("/api/users/profile");
        return ResponseEntity
                .created(uri)
                .body(user.get());
    }
}
