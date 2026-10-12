package com.smarttaskmanager.repository;

import com.smarttaskmanager.entity.FocusSession;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface FocusSessionRepository extends JpaRepository<FocusSession, Long> {
    List<FocusSession> findByUserIdOrderByCompletedAtDesc(Long userId);
    long countByUserIdAndCompletedAtAfter(Long userId, LocalDateTime after);
    List<FocusSession> findByUserIdAndCompletedAtAfter(Long userId, LocalDateTime after);
}
