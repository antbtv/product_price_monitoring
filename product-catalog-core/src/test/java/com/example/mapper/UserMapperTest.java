package com.example.mapper;

import com.example.dto.UserDTO;
import com.example.entity.security.User;
import com.example.enums.UserRole;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {

    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

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
        Assertions.assertNotNull(entity);
        Assertions.assertEquals(dto.getUserId(), entity.getUserId());
        Assertions.assertEquals(dto.getUsername(), entity.getUsername());
        Assertions.assertEquals(dto.getPassword(), entity.getPassword());
        Assertions.assertEquals(dto.getEmail(), entity.getEmail());
        Assertions.assertEquals(dto.getRole(), entity.getRole());
        Assertions.assertEquals(dto.getFirstName(), entity.getFirstName());
        Assertions.assertEquals(dto.getLastName(), entity.getLastName());
        Assertions.assertEquals(dto.getBirthDate(), entity.getBirthDate());
        Assertions.assertEquals(dto.getPhoneNumber(), entity.getPhoneNumber());
        Assertions.assertEquals(dto.getIsVerified(), entity.getIsVerified());
        Assertions.assertEquals(dto.getCreatedAt(), entity.getCreatedAt());
        Assertions.assertEquals(dto.getUpdatedAt(), entity.getUpdatedAt());
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
        Assertions.assertNotNull(dto);
        Assertions.assertEquals(entity.getUserId(), dto.getUserId());
        Assertions.assertEquals(entity.getUsername(), dto.getUsername());
        Assertions.assertEquals(entity.getPassword(), dto.getPassword());
        Assertions.assertEquals(entity.getEmail(), dto.getEmail());
        Assertions.assertEquals(entity.getRole(), dto.getRole());
        Assertions.assertEquals(entity.getFirstName(), dto.getFirstName());
        Assertions.assertEquals(entity.getLastName(), dto.getLastName());
        Assertions.assertEquals(entity.getBirthDate(), dto.getBirthDate());
        Assertions.assertEquals(entity.getPhoneNumber(), dto.getPhoneNumber());
        Assertions.assertEquals(entity.getIsVerified(), dto.getIsVerified());
        Assertions.assertEquals(entity.getCreatedAt(), dto.getCreatedAt());
        Assertions.assertEquals(entity.getUpdatedAt(), dto.getUpdatedAt());
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
        Assertions.assertEquals(2, entities.size());
        Assertions.assertEquals(dto1.getUserId(), entities.get(0).getUserId());
        Assertions.assertEquals(dto2.getUserId(), entities.get(1).getUserId());
        Assertions.assertEquals(dto1.getEmail(), entities.get(0).getEmail());
        Assertions.assertEquals(dto2.getPhoneNumber(), entities.get(1).getPhoneNumber());
        Assertions.assertEquals(dto1.getRole(), entities.get(0).getRole());
        Assertions.assertEquals(dto2.getFirstName(), entities.get(1).getFirstName());
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
        Assertions.assertEquals(2, dtos.size());
        Assertions.assertEquals(entity1.getUserId(), dtos.get(0).getUserId());
        Assertions.assertEquals(entity2.getUserId(), dtos.get(1).getUserId());
        Assertions.assertEquals(entity1.getUsername(), dtos.get(0).getUsername());
        Assertions.assertEquals(entity2.getEmail(), dtos.get(1).getEmail());
        Assertions.assertEquals(entity1.getRole(), dtos.get(0).getRole());
        Assertions.assertEquals(entity2.getLastName(), dtos.get(1).getLastName());
    }
}