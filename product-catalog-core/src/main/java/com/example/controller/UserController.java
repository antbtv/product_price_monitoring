package com.example.controller;

import com.example.dto.UserDTO;
import com.example.entity.security.User;
import com.example.mapper.UserMapper;
import com.example.service.security.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    private static final Logger logger = LogManager.getLogger(UserController.class);

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.userId")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody User user) {
        User existingUser = userService.getUserById(id);
        if (existingUser == null) {
            return ResponseEntity.notFound().build();
        }

        user.setUserId(id);
        userService.updateUser(user);

        UserDTO userDTO = userMapper.toDto(user);
        return ResponseEntity.ok(userDTO);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.userId")
    public ResponseEntity<UserDTO> partialUpdateUser(@PathVariable Long id,
                                                     @RequestBody UserDTO updateDTO) {
        User existingUser = userService.getUserById(id);
        if (existingUser == null) {
            return ResponseEntity.notFound().build();
        }

        if (updateDTO.getEmail() != null) {
            existingUser.setEmail(updateDTO.getEmail());
        }
        if (updateDTO.getRole() != null) {
            existingUser.setRole(updateDTO.getRole());
        }
        if (updateDTO.getFirstName() != null) {
            existingUser.setFirstName(updateDTO.getFirstName());
        }
        if (updateDTO.getLastName() != null) {
            existingUser.setLastName(updateDTO.getLastName());
        }
        if (updateDTO.getBirthDate() != null) {
            existingUser.setBirthDate(updateDTO.getBirthDate());
        }
        if (updateDTO.getPhoneNumber() != null) {
            existingUser.setPhoneNumber(updateDTO.getPhoneNumber());
        }
        if (updateDTO.getIsVerified() != null) {
            existingUser.setIsVerified(updateDTO.getIsVerified());
        }

        userService.updateUser(existingUser);
        UserDTO userDTO = userMapper.toDto(existingUser);
        return ResponseEntity.ok(userDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
