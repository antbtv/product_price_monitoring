package com.example.repository;

import com.example.entity.DataLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DataLogRepository extends JpaRepository<DataLog, Long> {
}
