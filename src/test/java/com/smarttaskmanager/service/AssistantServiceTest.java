package com.smarttaskmanager.service;

import com.smarttaskmanager.dto.RecommendationDto;
import com.smarttaskmanager.entity.Task;
import com.smarttaskmanager.entity.User;
import com.smarttaskmanager.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssistantServiceTest {

    @Mock TaskRepository taskRepository;
    @Mock TaskService taskService;

    @Test
    void overdueTask_isRecommendedFirst_withExplanation() {
        User u = new User();
        u.setId(1L);
        when(taskService.currentUser("tester")).thenReturn(u);
        Task overdue = new Task();
        overdue.setId(9L); overdue.setTitle("Late report");
        overdue.setPriority(Task.Priority.High);
        overdue.setStatus(Task.Status.TODO);
        overdue.setDeadline(LocalDate.now().minusDays(2));
        when(taskRepository.findByUserIdAndStatusNotOrderByDeadlineAsc(1L, Task.Status.COMPLETED))
                .thenReturn(List.of(overdue));

        RecommendationDto.PlanDto plan = new AssistantService(taskService, taskRepository).dailyPlan("tester");
        assertFalse(plan.getRecommendations().isEmpty());
        assertEquals("OVERDUE", plan.getRecommendations().get(0).getType());
        assertTrue(plan.getRecommendations().get(0).getReason().contains("Overdue"));
        assertNotNull(plan.getSummary());
    }

    @Test
    void emptyInbox_returnsCalmSummary() {
        User u = new User();
        u.setId(1L);
        when(taskService.currentUser("tester")).thenReturn(u);
        when(taskRepository.findByUserIdAndStatusNotOrderByDeadlineAsc(1L, Task.Status.COMPLETED))
                .thenReturn(List.of());
        RecommendationDto.PlanDto plan = new AssistantService(taskService, taskRepository).dailyPlan("tester");
        assertTrue(plan.getRecommendations().isEmpty());
        assertTrue(plan.getSummary().contains("Inbox zero"));
    }
}
