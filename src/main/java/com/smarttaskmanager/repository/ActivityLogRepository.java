package com.smarttaskmanager.repository;

import com.smarttaskmanager.entity.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    long countByUserIdAndActionAndCreatedAtAfter(Long userId, String action, LocalDateTime after);
    long countByUserIdAndAction(Long userId, String action);
}
