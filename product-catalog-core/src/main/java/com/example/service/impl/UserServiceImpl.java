package com.example.service.impl;

import com.example.dao.security.UserDao;
import com.example.entity.User;
import com.example.service.security.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
