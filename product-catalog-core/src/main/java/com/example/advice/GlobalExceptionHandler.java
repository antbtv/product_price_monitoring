package com.example.advice;

import com.example.exceptions.CategoryNotFoundException;
import com.example.exceptions.DataExportException;
import com.example.exceptions.DataImportException;
import com.example.exceptions.PriceHistoryNotFoundException;
import com.example.exceptions.PriceNotFoundException;
import com.example.exceptions.ProductNotFoundException;
import com.example.exceptions.StoreNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler({
            CategoryNotFoundException.class,
            PriceHistoryNotFoundException.class,
            PriceNotFoundException.class,
            ProductNotFoundException.class,
            StoreNotFoundException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundExceptions(RuntimeException ex) {
        String errorType = ex.getClass().getSimpleName()
                .replace("NotFoundException", "")
                .toUpperCase();

        log.warn("{}: {}", errorType, ex.getMessage());
        return Map.of(
                "error", errorType + "_NOT_FOUND",
                "message", ex.getMessage()
        );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Неверные имя пользователя или пароль");
    }

    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public Map<String, String> handleDataAccessException(DataAccessException ex) {
        log.error("Ошибка доступа к данным", ex);
        return Map.of(
                "error", "DATABASE_ERROR",
                "message", "Ошибка работы с базой данных"
        );
    }

    @ExceptionHandler(IOException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleIOException(IOException ex) {
        log.error("Ошибка ввода-вывода", ex);
        return Map.of(
                "error", "IO_ERROR",
                "message", "Ошибка обработки данных"
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleGeneralException(Exception ex) {
        log.error("Неожиданная ошибка", ex);
        return Map.of(
                "error", "INTERNAL_ERROR",
                "message", "Внутренняя ошибка сервера"
        );
    }

    @ExceptionHandler({DataExportException.class, DataImportException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleDataProcessingException(RuntimeException ex) {
        return Map.of(
                "error", "DATA_PROCESSING_ERROR",
                "message", ex.getMessage()
        );
    }

}