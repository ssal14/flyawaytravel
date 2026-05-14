package com.example.flyawaytravel.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class RequestUserDTO {

    @NotBlank
    @Email
    private String username;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Pattern(regexp = "^.*[A-Z].*$", message = "firstName debe contener al menos una letra mayúscula")
    private String firstName;

    @NotBlank
    @Pattern(regexp = "^.*[A-Z].*$", message = "lastName debe contener al menos una letra mayúscula")
    private String lastName;

    @NotBlank
    @Length(min = 8, message = "password debe tener al menos 8 caracteres")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$",
             message = "password debe contener al menos una letra y un número")
    private String password;
}
