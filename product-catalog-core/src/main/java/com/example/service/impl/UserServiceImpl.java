package com.example.service.impl;

import com.example.repository.security.UserRepository;
import com.example.entity.security.CustomUserDetails;
import com.example.entity.security.User;
import com.example.exceptions.InvalidTokenException;
import com.example.exceptions.UserAlreadyExistsException;
import com.example.exceptions.UserNotFoundException;
import com.example.service.security.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
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

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserDetailsService, UserService {

    @Value("${jwt.secret.key}")
    private String SECRET_KEY;
    private SecretKey secretKey;
    private static final long JWT_EXPIRATION_MS = 10 * 60 * 60 * 1000;

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    private SecretKey getSigningKey() {
        return secretKey;
    }

    @Transactional(readOnly = true)
    @Override
    public String generateToken(UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new UserNotFoundException(userDetails.getUsername()));

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION_MS);

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("userId", user.getUserId())
                .claim("roles", userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    @Transactional(readOnly = true)
    @Override
    public String extractUsername(String token) {
        try {
            SecretKey key = getSigningKey();

            Jwt<?, Claims> jwt = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);

            return jwt.getPayload().getSubject();
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
        return userRepository.findByUsername(username).orElse(null);
    }

    @Transactional(readOnly = true)
    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @Transactional
    @Override
    public void deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
        log.info("Удален пользователь ID={}", id);
    }

    @Transactional
    @Override
    public void updateUser(User user) {
        userRepository.findById(user.getUserId())
                .orElseThrow(() -> new UserNotFoundException(user.getUserId()));

        userRepository.save(user);
        log.info("Обновлен пользователь ID={}", user.getUserId());
    }

    @Transactional(readOnly = true)
    @Override
    public boolean isTokenExpired(String token) {
        try {
            SecretKey key = getSigningKey();

            Jwt<?, Claims> jwt = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);

            Date expiration = jwt.getPayload().getExpiration();
            return expiration.before(new Date());
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
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден: " + username));

        return new CustomUserDetails(
                user.getUserId(),
                user.getUsername(),
                user.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority(user.getRole().toString()))
        );
    }

    @Transactional(readOnly = true)
    @Override
    public boolean userExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    @Transactional
    @Override
    public void addUser(User user) {
        validateUser(user);
        if (userExists(user.getUsername())) {
            throw new UserAlreadyExistsException(user.getUsername());
        }
        userRepository.save(user);
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
