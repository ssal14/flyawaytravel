package com.example.flyawaytravel.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestAuthDTO {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;
}
