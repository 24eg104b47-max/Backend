package com.smarttaskmanager.service;

import com.smarttaskmanager.dto.RecommendationDto;
import com.smarttaskmanager.entity.Task;
import com.smarttaskmanager.entity.User;
import com.smarttaskmanager.repository.TaskRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class AssistantService {
    private final TaskService taskService;
    private final TaskRepository taskRepository;

    public AssistantService(TaskService taskService, TaskRepository taskRepository) {
        this.taskService = taskService; this.taskRepository = taskRepository;
    }

    private static int priorityWeight(Task.Priority p) {
        return switch (p) {
            case Critical -> 0;
            case High -> 1;
            case Medium -> 2;
            case Low -> 3;
        };
    }

    /** Deterministic rules over real task data: priority x deadline urgency. */
    public RecommendationDto.PlanDto dailyPlan(String username) {
        User u = taskService.currentUser(username);
        List<Task> open = taskRepository.findByUserIdAndStatusNotOrderByDeadlineAsc(
                u.getId(), Task.Status.COMPLETED);
        LocalDate today = LocalDate.now();
        List<RecommendationDto> recs = new ArrayList<>();

        List<Task> overdue = open.stream()
                .filter(t -> t.getDeadline() != null && t.getDeadline().isBefore(today)).toList();
        for (Task t : overdue) {
            recs.add(new RecommendationDto("OVERDUE", t.getTitle(),
                    "Overdue by " + ChronoUnit.DAYS.between(t.getDeadline(), today)
                            + " day(s). Reschedule or finish it first.", t.getId()));
        }
        List<Task> dueSoon = open.stream()
                .filter(t -> t.getDeadline() != null && !t.getDeadline().isBefore(today)
                        && t.getDeadline().isBefore(today.plusDays(3)))
                .sorted(Comparator.comparingInt(t -> priorityWeight(t.getPriority()))).toList();
        for (Task t : dueSoon) {
            recs.add(new RecommendationDto("DUE_SOON", t.getTitle(),
                    "Due " + t.getDeadline() + " with " + t.getPriority()
                            + " priority — schedule it today.", t.getId()));
        }
        open.stream()
                .filter(t -> t.getDeadline() == null)
                .sorted(Comparator.comparingInt(t -> priorityWeight(t.getPriority())))
                .limit(3).forEach(t -> recs.add(new RecommendationDto("NEXT_UP", t.getTitle(),
                        t.getPriority() + " priority with no deadline — good filler work.", t.getId())));
        open.stream().filter(t -> t.getEstimatedMinutes() != null && t.getEstimatedMinutes() > 120)
                .limit(2).forEach(t -> recs.add(new RecommendationDto("BREAK_DOWN", t.getTitle(),
                        "Estimated " + t.getEstimatedMinutes()
                                + " min. Break it into subtasks under 60 minutes each.", t.getId())));
        long est = open.stream().filter(t -> t.getEstimatedMinutes() != null)
                .mapToLong(Task::getEstimatedMinutes).sum();
        String summary = open.isEmpty() ? "Inbox zero — enjoy the calm, or create tomorrow's plan."
                : "You have " + open.size() + " open task(s), " + overdue.size()
                + " overdue, ~" + est + " min of estimated work. Aim for 3 focused wins today.";
        return new RecommendationDto.PlanDto(recs.stream().limit(8).toList(), summary);
    }
}
