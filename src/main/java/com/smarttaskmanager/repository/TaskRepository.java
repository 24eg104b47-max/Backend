package com.smarttaskmanager.repository;

import com.smarttaskmanager.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserIdOrderByCreatedAtDesc(Long userId);
    long countByUserId(Long userId);
    long countByUserIdAndStatus(Long userId, Task.Status status);
    long countByUserIdAndDemoTrue(Long userId);
    List<Task> findByUserIdAndDemoTrue(Long userId);
    List<Task> findByUserIdAndStatusNotOrderByDeadlineAsc(Long userId, Task.Status status);
    List<Task> findByUserIdAndDeadline(Long userId, LocalDate deadline);
    List<Task> findByUserIdAndDeadlineBeforeAndStatusNot(Long userId, LocalDate date, Task.Status status);
}
