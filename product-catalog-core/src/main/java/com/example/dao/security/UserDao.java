package com.example.dao.security;

import com.example.dao.GenericDao;
import com.example.entity.security.User;

public interface UserDao extends GenericDao<User> {

    User findByUsername(String username);
}
