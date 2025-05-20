package com.example.service.impl;

import com.example.dao.security.UserDao;
import com.example.entity.security.User;
import com.example.exceptions.InvalidTokenException;
import com.example.exceptions.UserAlreadyExistsException;
import com.example.exceptions.UserNotFoundException;
import com.example.service.security.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserDetailsService, UserService {

    @Value("${jwt.secret.key}")
    private String SECRET_KEY;

    private final UserDao userDAO;

    public UserServiceImpl(UserDao userDAO) {
        this.userDAO = userDAO;
    }

    @Transactional(readOnly = true)
    @Override
    public String generateToken(UserDetails userDetails) {
        User user = userDAO.findByUsername(userDetails.getUsername());
        if (user == null) {
            throw new UserNotFoundException(userDetails.getUsername());
        }

        String token = Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("userId", user.getUserId())
                .claim("roles", userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();

        log.info("Сгенерирован токен для пользователя: {}", userDetails.getUsername());
        return token;
    }

    @Transactional(readOnly = true)
    @Override
    public String extractUsername(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (JwtException e) {
            throw new InvalidTokenException("Невалидный токен", e);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
            log.warn("Текущий пользователь не аутентифицирован");
            return null;
        }

        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        return userDAO.findByUsername(username);
    }

    @Transactional(readOnly = true)
    @Override
    public User getUserById(Long id) {
        User user = userDAO.findById(id);
        if (user == null) {
            throw new UserNotFoundException(id);
        }
        return user;
    }

    @Transactional
    @Override
    public void deleteUser(Long id) {
        if (userDAO.findById(id) != null) {
            throw new UserNotFoundException(id);
        }
        userDAO.delete(id);
        log.info("Удален пользователь ID={}", id);
    }

    @Transactional
    @Override
    public void updateUser(User user) {
        if (userDAO.findById(user.getUserId()) != null) {
            throw new UserNotFoundException(user.getUserId());
        }
        userDAO.update(user);
        log.info("Обновлен пользователь ID={}", user.getUserId());
    }

    @Transactional(readOnly = true)
    @Override
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getExpiration().before(new Date());
        } catch (JwtException e) {
            throw new InvalidTokenException("Ошибка проверки токена", e);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (JwtException e) {
            log.warn("Невалидный токен: {}", e.getMessage());
            return false;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDAO.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Пользователь не найден: " + username);
        }

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority(user.getRole().toString()))
        );
    }

    @Transactional(readOnly = true)
    @Override
    public boolean userExists(String username) {
        return userDAO.findByUsername(username) != null;
    }

    @Transactional
    @Override
    public void addUser(User user) {
        validateUser(user);
        if (userExists(user.getUsername())) {
            throw new UserAlreadyExistsException(user.getUsername());
        }
        userDAO.create(user);
        log.info("Добавлен новый пользователь: {}", user.getUsername());
    }

    private void validateUser(User user) {
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Имя пользователя не может быть пустым");
        }
        if (user.getPassword() == null || user.getPassword().length() < 4) {
            throw new IllegalArgumentException("Пароль должен содержать не менее 4 символов");
        }
    }
}
