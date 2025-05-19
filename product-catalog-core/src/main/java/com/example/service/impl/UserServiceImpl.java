package com.example.service.impl;

import com.example.dao.security.UserDao;
import com.example.entity.security.User;
import com.example.service.security.UserService;
import io.jsonwebtoken.Claims;
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

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
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
        try {
            User user = userDAO.findByUsername(userDetails.getUsername());
            String token = Jwts.builder()
                    .setSubject(userDetails.getUsername())
                    .claim("userId", user.getUserId())
                    .claim("roles", userDetails.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.toList()))
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                    .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                    .compact();

            log.info("Сгенерирован токен для пользователя: {}", userDetails.getUsername());
            return token;
        } catch (Exception e) {
            log.error("Ошибка генерации токена для пользователя: {}. Ошибка: {}",
                    userDetails.getUsername(),
                    e.getMessage());
            throw new RuntimeException("Ошибка генерации токена", e);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public String extractUsername(String token) {
        try {
            String username = Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
            log.debug("Извлечено имя пользователя из токена: {}", username);
            return username;
        } catch (Exception e) {
            log.error("Ошибка извлечения имени пользователя из токена. Ошибка: {}", e.getMessage());
            throw new RuntimeException("Ошибка извлечения имени пользователя", e);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public User getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
                String username = ((UserDetails) authentication.getPrincipal()).getUsername();
                User user = userDAO.findByUsername(username);
                log.debug("Получен текущий пользователь: {}", username);
                return user;
            }
            log.warn("Текущий пользователь не аутентифицирован");
            return null;
        } catch (Exception e) {
            log.error("Ошибка получения текущего пользователя. Ошибка: {}", e.getMessage());
            throw new RuntimeException("Ошибка получения текущего пользователя", e);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public User getUserById(Long id) {
        try {
            User user = userDAO.findById(id);
            if (user == null) {
                log.warn("Пользователь с ID={} не найден", id);
                throw new RuntimeException("Пользователь не найден");
            }
            log.debug("Получен пользователь по ID: {}", id);
            return user;
        } catch (Exception e) {
            log.error("Ошибка получения пользователя ID={}. Ошибка: {}", id, e.getMessage());
            throw new RuntimeException("Ошибка получения пользователя", e);
        }
    }

    @Transactional
    @Override
    public void deleteUser(Long id) {
        try {
            userDAO.delete(id);
            log.info("Удален пользователь ID={}", id);
        } catch (Exception e) {
            log.error("Ошибка удаления пользователя ID={}. Ошибка: {}", id, e.getMessage());
            throw new RuntimeException("Ошибка удаления пользователя", e);
        }
    }

    @Transactional
    @Override
    public void updateUser(User user) {
        try {
            userDAO.update(user);
            log.info("Обновлен пользователь: ID={}, username={}",
                    user.getUserId(),
                    user.getUsername());
        } catch (Exception e) {
            log.error("Ошибка обновления пользователя ID={}. Ошибка: {}",
                    user.getUserId(),
                    e.getMessage());
            throw new RuntimeException("Ошибка обновления пользователя", e);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();
            boolean expired = claims.getExpiration().before(new Date());
            if (expired) {
                log.debug("Токен истек: {}", token);
            }
            return expired;
        } catch (Exception e) {
            log.error("Ошибка проверки срока действия токена. Ошибка: {}", e.getMessage());
            throw new RuntimeException("Ошибка проверки токена", e);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            boolean valid = username.equals(userDetails.getUsername()) && !isTokenExpired(token);
            log.debug("Проверка токена для пользователя {}. Результат: {}", username, valid);
            return valid;
        } catch (Exception e) {
            log.error("Ошибка валидации токена. Ошибка: {}", e.getMessage());
            throw new RuntimeException("Ошибка валидации токена", e);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            User user = userDAO.findByUsername(username);

            if (user == null) {
                log.warn("Пользователь не найден: {}", username);
                throw new UsernameNotFoundException(username);
            }

            Set<GrantedAuthority> authorities = new HashSet<>();
            authorities.add(new SimpleGrantedAuthority(user.getRole().toString()));

            log.debug("Загружены данные пользователя для аутентификации: {}", username);
            return new org.springframework.security.core.userdetails.User(
                    user.getUsername(),
                    user.getPassword(),
                    authorities);
        } catch (Exception e) {
            log.error("Ошибка загрузки пользователя {}. Ошибка: {}", username, e.getMessage());
            throw new UsernameNotFoundException("Ошибка загрузки пользователя", e);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public boolean userExists(String username) {
        try {
            boolean exists = userDAO.findByUsername(username) != null;
            log.debug("Проверка существования пользователя {}. Результат: {}", username, exists);
            return exists;
        } catch (Exception e) {
            log.error("Ошибка проверки существования пользователя {}. Ошибка: {}",
                    username,
                    e.getMessage());
            throw new RuntimeException("Ошибка проверки пользователя", e);
        }
    }

    @Transactional
    @Override
    public void addUser(User user) {
        try {
            if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
                throw new IllegalArgumentException("Имя пользователя не может быть пустым");
            }
            if (user.getPassword() == null || user.getPassword().length() < 4) {
                throw new IllegalArgumentException("Пароль должен содержать не менее 4 символов");
            }

            userDAO.create(user);
            log.info("Добавлен новый пользователь: {}", user.getUsername());
        } catch (Exception e) {
            log.error("Ошибка добавления пользователя {}. Ошибка: {}",
                    user.getUsername(),
                    e.getMessage());
            throw new RuntimeException("Ошибка добавления пользователя", e);
        }
    }
}
