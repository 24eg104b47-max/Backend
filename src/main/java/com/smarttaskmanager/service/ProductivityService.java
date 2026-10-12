package com.smarttaskmanager.service;

import com.smarttaskmanager.dto.*;
import com.smarttaskmanager.entity.*;
import com.smarttaskmanager.exception.ResourceNotFoundException;
import com.smarttaskmanager.repository.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class ProductivityService {
    private final TaskService taskService;
    private final TaskRepository taskRepository;
    private final ActivityLogRepository activityLogRepository;
    private final FocusSessionRepository focusSessionRepository;

    public ProductivityService(TaskService taskService, TaskRepository taskRepository,
                              ActivityLogRepository activityLogRepository,
                              FocusSessionRepository focusSessionRepository) {
        this.taskService = taskService;
        this.taskRepository = taskRepository;
        this.activityLogRepository = activityLogRepository;
        this.focusSessionRepository = focusSessionRepository;
    }

    public ProductivityDto productivity(String username) {
        User u = taskService.currentUser(username);
        Long uid = u.getId();
        List<Task> all = taskRepository.findByUserIdOrderByCreatedAtDesc(uid);

        // Weekly completion (last 7 days) from COMPLETED activity events
        List<Map<String, Object>> weekly = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate day = LocalDate.now().minusDays(i);
            LocalDateTime start = day.atStartOfDay();
            LocalDateTime end = day.plusDays(1).atStartOfDay();
            long count = activityLogRepository
                    .countByUserIdAndActionAndCreatedAtAfter(uid, "COMPLETED", start.minusSeconds(1));
            // subtract events after end of day
            long afterEnd = activityLogRepository
                    .countByUserIdAndActionAndCreatedAtAfter(uid, "COMPLETED", end.minusSeconds(1));
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("day", day.getDayOfWeek().toString().substring(0, 3));
            m.put("completed", Math.max(0, count - afterEnd));
            weekly.add(m);
        }

        Map<String, Long> byPriority = new LinkedHashMap<>();
        Map<String, Long> byStatus = new LinkedHashMap<>();
        for (Task t : all) {
            byPriority.merge(t.getPriority().name(), 1L, Long::sum);
            byStatus.merge(t.getStatus().name(), 1L, Long::sum);
        }
        long overdue = taskRepository.findByUserIdAndDeadlineBeforeAndStatusNot(
                uid, LocalDate.now(), Task.Status.COMPLETED).size();
        long dueToday = taskRepository.findByUserIdAndDeadline(uid, LocalDate.now()).stream()
                .filter(t -> t.getStatus() != Task.Status.COMPLETED).count();
        long focusToday = focusSessionRepository.findByUserIdAndCompletedAtAfter(
                uid, LocalDate.now().atStartOfDay()).stream()
                .mapToLong(FocusSession::getMinutesActual).sum();
        return new ProductivityDto(weekly, byPriority, byStatus, overdue, dueToday, focusToday);
    }
}
