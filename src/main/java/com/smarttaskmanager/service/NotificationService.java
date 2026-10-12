package com.smarttaskmanager.service;

import com.smarttaskmanager.dto.NotificationDto;
import com.smarttaskmanager.entity.Notification;
import com.smarttaskmanager.entity.Task;
import com.smarttaskmanager.entity.User;
import com.smarttaskmanager.exception.ResourceNotFoundException;
import com.smarttaskmanager.repository.NotificationRepository;
import com.smarttaskmanager.repository.TaskRepository;
import com.smarttaskmanager.repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class NotificationService {
    private final NotificationRepository repo;
    private final TaskService taskService;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository repo, TaskService taskService,
                              TaskRepository taskRepository, UserRepository userRepository) {
        this.repo = repo; this.taskService = taskService;
        this.taskRepository = taskRepository; this.userRepository = userRepository;
    }

    public List<NotificationDto> list(String username) {
        User u = taskService.currentUser(username);
        return repo.findByUserIdOrderByCreatedAtDesc(u.getId()).stream()
                .map(n -> new NotificationDto(n.getId(), n.getTitle(), n.getMessage(), n.getKind(), n.isRead()))
                .toList();
    }

    public long unreadCount(String username) {
        return repo.countByUserIdAndReadFalse(taskService.currentUser(username).getId());
    }

    public void markRead(String username, Long id) {
        Notification n = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        User u = taskService.currentUser(username);
        if (!n.getUser().getId().equals(u.getId())) throw new AccessDeniedException("Not yours");
        n.setRead(true);
    }

    public void notify(User user, String title, String message, String kind) {
        Notification n = new Notification();
        n.setUser(user); n.setTitle(title); n.setMessage(message); n.setKind(kind);
        repo.save(n);
    }

    /** Daily reminder job: warns owners about overdue + due-today tasks (server must be running). */
    @Scheduled(cron = "0 0 8 * * *")
    public void dailyReminders() {
        for (User u : userRepository.findAll()) {
            List<Task> overdue = taskRepository.findByUserIdAndDeadlineBeforeAndStatusNot(
                    u.getId(), LocalDate.now(), Task.Status.COMPLETED);
            if (!overdue.isEmpty()) {
                notify(u, "Overdue tasks need attention",
                        "You have " + overdue.size() + " overdue task(s). Open My Tasks to replan.",
                        "OVERDUE");
            }
            long dueToday = taskRepository.findByUserIdAndDeadline(u.getId(), LocalDate.now())
                    .stream().filter(t -> t.getStatus() != Task.Status.COMPLETED).count();
            if (dueToday > 0) {
                notify(u, "Tasks due today", dueToday + " task(s) are due today. Good luck!",
                        "REMINDER");
            }
        }
    }
}
