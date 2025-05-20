package com.example.service.security;

import com.example.entity.security.User;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Интерфейс для управления пользователями
 */
public interface UserService {

    /**
     * Генерация JWT-токена для пользователя
     *
     * @param userDetails данные пользователя
     * @return токен
     */
    String generateToken(UserDetails userDetails);

    /**
     * Извлекает имя пользователя из полученного токена
     *
     * @param token токен
     * @return имя пользователя
     */
    String extractUsername(String token);

    /**
     * Проверяет, истек ли срок токена
     *
     * @param token токен
     * @return true, если токен истек; false, если нет
     */
    boolean isTokenExpired(String token);

    /**
     * Проверяет, действителен ли токен для пользователя
     *
     * @param token токен
     * @param userDetails пользователь
     * @return true, если токен действителен; false, если нет
     */
    boolean validateToken(String token, UserDetails userDetails);

    /**
     * Проверяет, существует ли пользователь с заданным именем
     *
     * @param username пользователь
     * @return true, если существует; false, если нет
     */
    boolean userExists(String username);

    /**
     * Добавление пользователя
     *
     * @param user сущность пользователя
     */
    void addUser(User user);

    /**
     * Поиск пользователя по имени
     *
     * @param username имя пользователя
     * @return данные пользователя
     */
    UserDetails loadUserByUsername(String username);

    /**
     * Получение текущего пользователя
     *
     * @return пользователь
     */
    User getCurrentUser();

    /**
     * Получение пользователя по id
     *
     * @param id id пользователя
     * @return сущность пользователя
     */
    User getUserById(Long id);

    /**
     * Удаление пользователя
     *
     * @param id id пользователя
     */
    void deleteUser(Long id);

    /**
     * Обновление пользователя
     *
     * @param user сущность пользователя
     */
    void updateUser(User user);
}
