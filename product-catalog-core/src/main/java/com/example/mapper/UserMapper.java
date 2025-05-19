package com.example.mapper;

import com.example.dto.StoreDTO;
import com.example.dto.UserDTO;
import com.example.entity.Store;
import com.example.entity.security.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    
    User toEntity(UserDTO userDTO);

    UserDTO toDto(User user);

    List<User> toEntityList(List<UserDTO> userDTOS);

    List<UserDTO> toDtoList(List<User> users);
}
