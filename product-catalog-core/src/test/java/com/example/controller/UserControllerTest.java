package com.example.controller;

import com.example.advice.GlobalExceptionHandler;
import com.example.dto.UserDTO;
import com.example.entity.security.User;
import com.example.mapper.UserMapper;
import com.example.service.security.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private final LocalDateTime testTime = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void testUpdateUser_Success() throws Exception {
        // GIVEN
        Long userId = 1L;
        User user = new User();
        user.setUserId(userId);
        user.setUsername("updatedUser");

        User updatedUser = new User();
        updatedUser.setUserId(userId);
        updatedUser.setUsername("updatedUser");

        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(userId);
        userDTO.setUsername("updatedUser");

        when(userService.getUserById(userId)).thenReturn(user);
        when(userMapper.toDto(any(User.class))).thenReturn(userDTO);
        doNothing().when(userService).updateUser(any(User.class));

        // WHEN
        mockMvc.perform(put("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.username").value("updatedUser"));

        //THEN
        verify(userService).updateUser(any(User.class));
    }

    @Test
    void testUpdateUser_NotFound() throws Exception {
        // GIVEN
        Long userId = 999L;
        when(userService.getUserById(userId)).thenReturn(null);

        // WHEN
        mockMvc.perform(put("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new User())))
                .andExpect(status().isNotFound());

        // THEN
        verify(userService, never()).updateUser(any());
    }

    @Test
    void testPartialUpdateUser_Success() throws Exception {
        // GIVEN
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setUserId(userId);
        existingUser.setUsername("oldUser");

        UserDTO updateDTO = new UserDTO();
        updateDTO.setFirstName("New Name");
        updateDTO.setEmail("new@email.com");

        User updatedUser = new User();
        updatedUser.setUserId(userId);
        updatedUser.setUsername("oldUser");
        updatedUser.setFirstName("New Name");
        updatedUser.setEmail("new@email.com");

        UserDTO responseDTO = new UserDTO();
        responseDTO.setUserId(userId);
        responseDTO.setFirstName("New Name");

        when(userService.getUserById(userId)).thenReturn(existingUser);
        when(userMapper.toDto(any())).thenReturn(responseDTO);

        // WHEN
        mockMvc.perform(patch("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("New Name"));

        //THEN
        verify(userService).updateUser(existingUser);
    }

    @Test
    void testDeleteUser_Success() throws Exception {
        // GIVEN
        Long userId = 1L;
        when(userService.getUserById(userId)).thenReturn(new User());

        // WHEN
        mockMvc.perform(delete("/users/{id}", userId))
                .andExpect(status().isNoContent());

        //THEN
        verify(userService).deleteUser(userId);
    }

    @Test
    void testDeleteUser_NotFound() throws Exception {
        // GIVEN
        Long userId = 999L;
        when(userService.getUserById(userId)).thenReturn(null);

        // WHEN & THEN
        mockMvc.perform(delete("/users/{id}", userId))
                .andExpect(status().isNotFound());

        //THEN
        verify(userService, never()).deleteUser(any());
    }

    @Test
    void testGetUser_Success() throws Exception {
        // GIVEN
        Long userId = 1L;
        User user = new User();
        user.setUserId(userId);
        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(userId);

        when(userService.getUserById(userId)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDTO);

        // WHEN & THEN
        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId));
    }

    @Test
    void testGetUser_NotFound() throws Exception {
        // GIVEN
        Long userId = 999L;
        when(userService.getUserById(userId)).thenReturn(null);

        // WHEN & THEN
        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testPartialUpdateUser_EmptyBody() throws Exception {
        // GIVEN
        Long userId = 1L;
        when(userService.getUserById(userId)).thenReturn(new User());

        // WHEN
        mockMvc.perform(patch("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk());

        // THEN
        verify(userService).updateUser(any());
    }

    @Test
    void testUpdateUser_MismatchId() throws Exception {
        // GIVEN
        Long pathId = 1L;
        User user = new User();
        user.setUserId(2L);

        // WHEN & THEN
        mockMvc.perform(put("/users/{id}", pathId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isNotFound());
    }
}