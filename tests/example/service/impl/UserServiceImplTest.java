package com.example.service.impl;

import com.example.entity.security.CustomUserDetails;
import com.example.enums.UserRole;
import com.example.repository.security.UserRepository;
import com.example.entity.security.User;
import com.example.exceptions.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Disabled
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(userService, "SECRET_KEY", "test-secret-key");
    }

    @Test
    void testGenerateToken() {
        // GIVEN
        User mockUser = new User();
        mockUser.setUserId(1L);
        mockUser.setUsername("testUser");
        mockUser.setPassword("password");
        mockUser.setRole(UserRole.ROLE_USER);
        Mockito.when(userRepository.findByUsername("testUser")).thenReturn(mockUser);

        UserDetails userDetails = new org.springframework.security.core.userdetails.User("testUser", "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));

        // WHEN
        String token = userService.generateToken(userDetails);

        // THEN
        Assertions.assertNotNull(token);
        Assertions.assertFalse(token.isEmpty());
    }

    @Test
    void testExtractUsername() {
        // GIVEN
        String username = "testUser";

        User mockUser = new User();
        mockUser.setUserId(1L);
        mockUser.setUsername(username);
        mockUser.setPassword("password");
        mockUser.setRole(UserRole.ROLE_USER);
        Mockito.when(userRepository.findByUsername(username)).thenReturn(mockUser);

        String token = userService.generateToken(new org.springframework.security.core.userdetails.User(username, "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))));

        // WHEN
        String extractedUsername = userService.extractUsername(token);

        // THEN
        Assertions.assertEquals(username, extractedUsername);
    }

    @Test
    void testIsTokenExpired() {
        // GIVEN
        String username = "testUser";

        User mockUser = new User();
        mockUser.setUserId(1L);
        mockUser.setUsername(username);
        mockUser.setPassword("password");
        mockUser.setRole(UserRole.ROLE_USER);
        Mockito.when(userRepository.findByUsername(username)).thenReturn(mockUser);

        String token = userService.generateToken(new org.springframework.security.core.userdetails.User("testUser", "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))));

        // WHEN
        boolean isExpired = userService.isTokenExpired(token);

        // THEN
        Assertions.assertFalse(isExpired);
    }

    @Test
    void testValidateToken() {
        // GIVEN
        String username = "testUser";

        User mockUser = new User();
        mockUser.setUserId(1L);
        mockUser.setUsername(username);
        mockUser.setPassword("password");
        mockUser.setRole(UserRole.ROLE_USER);
        Mockito.when(userRepository.findByUsername(username)).thenReturn(mockUser);

        UserDetails userDetails = new org.springframework.security.core.userdetails.User("testUser", "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        String token = userService.generateToken(userDetails);

        // WHEN
        boolean isValid = userService.validateToken(token, userDetails);

        // THEN
        Assertions.assertTrue(isValid);
    }

    @Test
    void testLoadUserByUsername_UserExists() {
        // GIVEN
        String username = "testUser";
        User user = new User();
        user.setUsername(username);
        user.setPassword("password");
        user.setRole(UserRole.ROLE_USER);
        Mockito.when(userRepository.findByUsername(username)).thenReturn(user);

        // WHEN
        UserDetails result = userService.loadUserByUsername(username);

        // THEN
        Assertions.assertNotNull(result);
        Assertions.assertEquals(username, result.getUsername());
        Mockito.verify(userRepository).findByUsername(username);
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        // GIVEN
        String username = "nonExistentUser";
        Mockito.when(userRepository.findByUsername(username)).thenReturn(null);

        // WHEN & THEN
        Assertions.assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(username));
    }

    @Test
    void testUserExists_True() {
        // GIVEN
        String username = "testUser";
        User user = new User();
        user.setUsername(username);
        Mockito.when(userRepository.findByUsername(username)).thenReturn(user);

        // WHEN
        boolean exists = userService.userExists(username);

        // THEN
        Assertions.assertTrue(exists);
        Mockito.verify(userRepository).findByUsername(username);
    }

    @Test
    void testUserExists_False() {
        // GIVEN
        String username = "nonExistentUser";
        Mockito.when(userRepository.findByUsername(username)).thenReturn(null);

        // WHEN
        boolean exists = userService.userExists(username);

        // THEN
        Assertions.assertFalse(exists);
        Mockito.verify(userRepository).findByUsername(username);
    }

    @Test
    void testAddUser_ValidUser() {
        // GIVEN
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("password");

        // WHEN
        userService.addUser(user);

        // THEN
        Mockito.verify(userRepository).create(user);
    }

    @Test
    void testAddUser_EmptyUsername() {
        // GIVEN
        User user = new User();
        user.setUsername("");
        user.setPassword("password");

        // WHEN & THEN
        Assertions.assertThrows(IllegalArgumentException.class, () -> userService.addUser(user));
    }

    @Test
    void testAddUser_ShortPassword() {
        // GIVEN
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("123");

        // WHEN & THEN
        Assertions.assertThrows(IllegalArgumentException.class, () -> userService.addUser(user));
    }

    @Test
    void testGetCurrentUser_Authenticated() {
        // GIVEN
        User mockUser = new User();
        mockUser.setUsername("testUser");
        mockUser.setPassword("password");

        UserDetails userDetails = new CustomUserDetails(
                1L,
                "testUser",
                "password",
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
        );

        Authentication auth = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        Mockito.when(userRepository.findByUsername("testUser")).thenReturn(mockUser);

        // WHEN
        User result = userService.getCurrentUser();

        // THEN
        Assertions.assertNotNull(result);
        Assertions.assertEquals("testUser", result.getUsername());
    }

    @Test
    void testGetCurrentUser_NotAuthenticated() {
        // GIVEN
        SecurityContextHolder.clearContext();

        // WHEN
        User result = userService.getCurrentUser();

        // THEN
        Assertions.assertNull(result);
    }

    @Test
    void testGetUserById_Exists() {
        // GIVEN
        Long userId = 1L;
        User expectedUser = new User();
        expectedUser.setUserId(userId);
        Mockito.when(userRepository.findById(userId)).thenReturn(expectedUser);

        // WHEN
        User result = userService.getUserById(userId);

        // THEN
        Assertions.assertNotNull(result);
        Assertions.assertEquals(userId, result.getUserId());
        Mockito.verify(userRepository).findById(userId);
    }

    @Test
    void testGetUserById_NotExists() {
        // GIVEN
        Long userId = 999L;
        Mockito.when(userRepository.findById(userId)).thenReturn(null);

        // WHEN & THEN
        Assertions.assertThrows(UserNotFoundException.class, () -> userService.getUserById(userId));
    }

    @Test
    void testDeleteUser_Success() {
        // GIVEN
        Long userId = 1L;
        User mockUser = new User();
        mockUser.setUserId(userId);
        Mockito.when(userRepository.findById(userId)).thenReturn(mockUser);

        // WHEN
        userService.deleteUser(userId);

        // THEN
        Mockito.verify(userRepository).delete(userId);
    }

    @Test
    void testDeleteUser_UserNotFound() {
        // GIVEN
        Long userId = 999L;
        Mockito.when(userRepository.findById(userId)).thenReturn(null);

        // WHEN & THEN
        Assertions.assertThrows(UserNotFoundException.class, () -> userService.deleteUser(userId));
        Mockito.verify(userRepository, Mockito.never()).delete(ArgumentMatchers.any());
    }

    @Test
    void testUpdateUser_Success() {
        // GIVEN
        User user = new User();
        user.setUserId(1L);
        user.setUsername("updatedUser");
        Mockito.when(userRepository.findById(1L)).thenReturn(user);

        // WHEN
        userService.updateUser(user);

        // THEN
        Mockito.verify(userRepository).update(user);
    }

    @Test
    void testUpdateUser_UserNotFound() {
        // GIVEN
        User user = new User();
        user.setUserId(999L);
        Mockito.when(userRepository.findById(999L)).thenReturn(null);

        // WHEN & THEN
        Assertions.assertThrows(UserNotFoundException.class, () -> userService.updateUser(user));
        Mockito.verify(userRepository, Mockito.never()).update(ArgumentMatchers.any());
    }
}
