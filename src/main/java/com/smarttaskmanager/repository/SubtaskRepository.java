package com.smarttaskmanager.repository;

import com.smarttaskmanager.entity.Subtask;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SubtaskRepository extends JpaRepository<Subtask, Long> {
    List<Subtask> findByTaskId(Long taskId);
    void deleteByTaskId(Long taskId);
}
