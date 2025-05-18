package com.example.mapper;

import com.example.dto.UserDTO;
import com.example.entity.security.User;
import com.example.enums.UserRole;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {

    private final UserMapper userMapper = UserMapper.INSTANCE;

    @Test
    void testToEntity() {
        // GIVEN
        LocalDateTime testTime = LocalDateTime.now();
        UserDTO dto = new UserDTO(
                1L,
                "testUser",
                "securePassword",
                "user@example.com",
                UserRole.ROLE_USER,
                "Иван",
                "Иванов",
                LocalDate.of(1990, 5, 15),
                "+79123456789",
                true,
                testTime,
                testTime
        );

        // WHEN
        User entity = userMapper.toEntity(dto);

        // THEN
        assertNotNull(entity);
        assertEquals(dto.getUserId(), entity.getUserId());
        assertEquals(dto.getUsername(), entity.getUsername());
        assertEquals(dto.getPassword(), entity.getPassword());
        assertEquals(dto.getEmail(), entity.getEmail());
        assertEquals(dto.getRole(), entity.getRole());
        assertEquals(dto.getFirstName(), entity.getFirstName());
        assertEquals(dto.getLastName(), entity.getLastName());
        assertEquals(dto.getBirthDate(), entity.getBirthDate());
        assertEquals(dto.getPhoneNumber(), entity.getPhoneNumber());
        assertEquals(dto.getIsVerified(), entity.getIsVerified());
        assertEquals(dto.getCreatedAt(), entity.getCreatedAt());
        assertEquals(dto.getUpdatedAt(), entity.getUpdatedAt());
    }

    @Test
    void testToDto() {
        // GIVEN
        LocalDateTime testTime = LocalDateTime.now();
        User entity = new User();
        entity.setUserId(2L);
        entity.setUsername("adminUser");
        entity.setPassword("adminPass");
        entity.setEmail("admin@example.com");
        entity.setRole(UserRole.ROLE_ADMIN);
        entity.setFirstName("Алексей");
        entity.setLastName("Петров");
        entity.setBirthDate(LocalDate.of(1985, 10, 20));
        entity.setPhoneNumber("+79234567890");
        entity.setIsVerified(true);
        entity.setCreatedAt(testTime);
        entity.setUpdatedAt(testTime);

        // WHEN
        UserDTO dto = userMapper.toDto(entity);

        // THEN
        assertNotNull(dto);
        assertEquals(entity.getUserId(), dto.getUserId());
        assertEquals(entity.getUsername(), dto.getUsername());
        assertEquals(entity.getPassword(), dto.getPassword());
        assertEquals(entity.getEmail(), dto.getEmail());
        assertEquals(entity.getRole(), dto.getRole());
        assertEquals(entity.getFirstName(), dto.getFirstName());
        assertEquals(entity.getLastName(), dto.getLastName());
        assertEquals(entity.getBirthDate(), dto.getBirthDate());
        assertEquals(entity.getPhoneNumber(), dto.getPhoneNumber());
        assertEquals(entity.getIsVerified(), dto.getIsVerified());
        assertEquals(entity.getCreatedAt(), dto.getCreatedAt());
        assertEquals(entity.getUpdatedAt(), dto.getUpdatedAt());
    }

    @Test
    void testToEntityList() {
        // GIVEN
        LocalDateTime testTime = LocalDateTime.now();
        UserDTO dto1 = new UserDTO(1L, "user1", "pass1", "user1@test.com",
                UserRole.ROLE_USER, "John", "Doe", LocalDate.of(1990, 1, 1),
                "+111111111", true, testTime, testTime);

        UserDTO dto2 = new UserDTO(2L, "user2", "pass2", "user2@test.com",
                UserRole.ROLE_ADMIN, "Jane", "Smith", LocalDate.of(1995, 2, 2),
                "+222222222", false, testTime, testTime);

        List<UserDTO> dtos = List.of(dto1, dto2);

        // WHEN
        List<User> entities = userMapper.toEntityList(dtos);

        // THEN
        assertEquals(2, entities.size());
        assertEquals(dto1.getUserId(), entities.get(0).getUserId());
        assertEquals(dto2.getUserId(), entities.get(1).getUserId());
        assertEquals(dto1.getEmail(), entities.get(0).getEmail());
        assertEquals(dto2.getPhoneNumber(), entities.get(1).getPhoneNumber());
        assertEquals(dto1.getRole(), entities.get(0).getRole());
        assertEquals(dto2.getFirstName(), entities.get(1).getFirstName());
    }

    @Test
    void testToDtoList() {
        // GIVEN
        LocalDateTime testTime = LocalDateTime.now();

        User entity1 = new User();
        entity1.setUserId(1L);
        entity1.setUsername("test1");
        entity1.setEmail("test1@example.com");
        entity1.setRole(UserRole.ROLE_USER);
        entity1.setFirstName("Test");
        entity1.setLastName("User");
        entity1.setCreatedAt(testTime);
        entity1.setUpdatedAt(testTime);

        User entity2 = new User();
        entity2.setUserId(2L);
        entity2.setUsername("test2");
        entity2.setEmail("test2@example.com");
        entity2.setRole(UserRole.ROLE_ADMIN);
        entity2.setFirstName("Admin");
        entity2.setLastName("User");
        entity2.setCreatedAt(testTime);
        entity2.setUpdatedAt(testTime);

        List<User> entities = List.of(entity1, entity2);

        // WHEN
        List<UserDTO> dtos = userMapper.toDtoList(entities);

        // THEN
        assertEquals(2, dtos.size());
        assertEquals(entity1.getUserId(), dtos.get(0).getUserId());
        assertEquals(entity2.getUserId(), dtos.get(1).getUserId());
        assertEquals(entity1.getUsername(), dtos.get(0).getUsername());
        assertEquals(entity2.getEmail(), dtos.get(1).getEmail());
        assertEquals(entity1.getRole(), dtos.get(0).getRole());
        assertEquals(entity2.getLastName(), dtos.get(1).getLastName());
    }
}