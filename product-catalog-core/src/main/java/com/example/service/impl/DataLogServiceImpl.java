package com.example.service.impl;

import com.example.dao.DataLogDao;
import com.example.entity.DataLog;
import com.example.entity.security.User;
import com.example.service.DataLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class DataLogServiceImpl implements DataLogService {
    private final DataLogDao dataLogDao;

    public DataLogServiceImpl(DataLogDao dataLogDao) {
        this.dataLogDao = dataLogDao;
    }

    @Transactional
    @Override
    public void logOperation(String operationType, String tableName, Long recordCount, User user) {
        DataLog log = new DataLog();
        log.setOperationType(operationType);
        log.setTableName(tableName);
        log.setRecordCount(recordCount);
        log.setUser(user);
        log.setOperationTime(LocalDateTime.now());
        dataLogDao.create(log);
    }
}
