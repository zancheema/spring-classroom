package com.zancheema.classroom.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupPayload {
    private @NotBlank String firstName;
    private @NotBlank String lastName;
    private @NotBlank @Email String email;
    private @NotBlank String password;
}
