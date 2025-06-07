package com.example.service.impl;

import com.example.entity.DataLog;
import com.example.entity.security.User;
import com.example.repository.DataLogRepository;
import com.example.service.DataLogService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@AllArgsConstructor
public class DataLogServiceImpl implements DataLogService {
    private final DataLogRepository dataLogRepository;

    @Transactional
    @Override
    public void logOperation(String operationType, String tableName, Long recordCount, User user) {
        DataLog dataLog = new DataLog();
        dataLog.setOperationType(operationType);
        dataLog.setTableName(tableName);
        dataLog.setRecordCount(recordCount);
        dataLog.setUser(user);
        dataLog.setOperationTime(LocalDateTime.now());
        dataLogRepository.save(dataLog);
    }
}
