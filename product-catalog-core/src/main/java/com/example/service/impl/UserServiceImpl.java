package com.example.service.impl;

import com.example.dao.security.UserDao;
import com.example.dto.UserDTO;
import com.example.entity.security.User;
import com.example.mapper.UserMapper;
import com.example.service.security.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Transactional
    @Override
    public void createUser(User user) {
        userDao.create(user);
    }

    @Transactional(readOnly = true)
    @Override
    public User getUserById(Long id) {
        return userDao.findById(id);
    }

    @Transactional
    @Override
    public void updateUser(User user) {
        user.setUpdatedAt(LocalDateTime.now());
        userDao.update(user);
    }

    @Transactional
    @Override
    public void deleteUser(Long id) {
        userDao.delete(id);
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    @Transactional(readOnly = true)
    public void exportUsersToJson(String filePath) throws IOException {
        List<User> users = userDao.findAll();
        List<UserDTO> userDTOS = UserMapper.INSTANCE.toDtoList(users);

        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        objectMapper.writeValue(new File(filePath), userDTOS);
    }
}
