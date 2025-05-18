package com.example.controller;

import com.example.dto.LoginDTO;
import com.example.dto.RegisterDTO;
import com.example.entity.security.User;
import com.example.enums.UserRole;
import com.example.service.security.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/auth", produces = "application/json; charset=UTF-8")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserService userService, AuthenticationManager authenticationManager,
                          PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDTO loginDTO) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.getUsername(),
                            loginDTO.getPassword())
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String jwt = userService.generateToken(userDetails);
            return ResponseEntity.ok(jwt);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Неверный логин или пароль");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Ошибка при входе " + e);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterDTO registerDTO) {
        try {
            if (userService.userExists(registerDTO.getUsername())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Такой пользователь уже существует");
            }

            User newUser = new User();
            newUser.setUsername(registerDTO.getUsername());
            newUser.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
            newUser.setRole(UserRole.valueOf(registerDTO.getRole().toUpperCase()));
            newUser.setEmail(registerDTO.getEmail());
            newUser.setFirstName(registerDTO.getFirstName());
            newUser.setLastName(registerDTO.getLastName());
            newUser.setBirthDate(registerDTO.getBirthDate());
            newUser.setPhoneNumber(registerDTO.getPhoneNumber());
            newUser.setIsVerified(false);

            userService.addUser(newUser);

            return ResponseEntity.status(HttpStatus.CREATED).body("Пользователь успешно зарегистрирован");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при регистрации: "
                    + e.getMessage());
        }
    }
}
