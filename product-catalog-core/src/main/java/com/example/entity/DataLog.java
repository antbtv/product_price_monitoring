package com.example.entity;

import com.example.entity.security.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "data_log")
@Getter
@Setter
@NoArgsConstructor
public class DataLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long logId;

    @Column(name = "operation_type", nullable = false)
    private String operationType;

    @Column(name = "operation_time", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime operationTime;

    @Column(name = "table_name", nullable = false)
    private String tableName;

    @Column(name = "record_count", nullable = false)
    private Long recordCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @PrePersist
    protected void onCreate() {
        operationTime = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "DataLog{" +
                "logId=" + logId +
                ", operationType='" + operationType + '\'' +
                ", operationTime=" + operationTime +
                ", tableName='" + tableName + '\'' +
                ", recordCount=" + recordCount +
                ", userId=" + user.getUserId() +
                '}';
    }
}