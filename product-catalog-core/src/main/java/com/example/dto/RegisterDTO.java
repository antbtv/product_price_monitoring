package com.example.dto;

import com.example.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDTO {

    private String username;
    private String password;
    private String email;
    private String role;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String phoneNumber;
}
