package com.smarttaskmanager.service;

import com.smarttaskmanager.dto.TaskDto;
import com.smarttaskmanager.entity.Task;
import com.smarttaskmanager.entity.User;
import com.smarttaskmanager.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock TaskRepository taskRepository;
    @Mock UserRepository userRepository;
    @Mock ProjectRepository projectRepository;
    @Mock SubtaskRepository subtaskRepository;
    @Mock ActivityLogRepository activityLogRepository;

    TaskService taskService;
    User user;

    @BeforeEach
    void setUp() {
        taskService = new TaskService(taskRepository, userRepository, projectRepository,
                subtaskRepository, activityLogRepository);
        user = new User();
        user.setId(1L);
        user.setUsername("tester");
        lenient().when(userRepository.findByUsername("tester")).thenReturn(Optional.of(user));
    }

    @Test
    void parseStatus_acceptsLegacyAliases() {
        assertEquals(Task.Status.TODO, TaskService.parseStatus("Pending"));
        assertEquals(Task.Status.IN_PROGRESS, TaskService.parseStatus("In Progress"));
        assertEquals(Task.Status.IN_PROGRESS, TaskService.parseStatus("In_Progress"));
        assertEquals(Task.Status.COMPLETED, TaskService.parseStatus("Completed"));
        assertEquals(Task.Status.IN_REVIEW, TaskService.parseStatus("In Review"));
        assertEquals(Task.Status.TODO, TaskService.parseStatus(null));
    }

    @Test
    void create_persistsOwnedTask() {
        TaskDto dto = new TaskDto();
        dto.setTitle("Test task");
        dto.setPriority("High");
        dto.setStatus("TODO");
        dto.setDeadline(LocalDate.now().plusDays(1));
        when(taskRepository.save(any(Task.class))).thenAnswer(i -> {
            Task t = i.getArgument(0);
            t.setId(42L);
            return t;
        });
        TaskDto out = taskService.create("tester", dto);
        assertEquals(42L, out.getId());
        assertEquals("High", out.getPriority());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void completingTask_logsSingleCompletedEvent() {
        Task t = new Task();
        t.setId(7L); t.setTitle("T"); t.setPriority(Task.Priority.Medium);
        t.setStatus(Task.Status.IN_PROGRESS); t.setUser(user);
        when(taskRepository.findById(7L)).thenReturn(Optional.of(t));
        when(taskRepository.save(any(Task.class))).thenAnswer(i -> i.getArgument(0));
        taskService.patchStatus("tester", 7L, "COMPLETED");
        verify(activityLogRepository).save(argThat(l -> "COMPLETED".equals(l.getAction())));
    }

    @Test
    void list_returnsOwnedTasksOnly() {
        when(taskRepository.findByUserIdOrderByCreatedAtDesc(1L)).thenReturn(List.of());
        assertTrue(taskService.list("tester").isEmpty());
    }
}
