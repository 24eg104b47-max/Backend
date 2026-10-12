package com.smarttaskmanager.service;

import com.smarttaskmanager.dto.*;
import com.smarttaskmanager.entity.*;
import com.smarttaskmanager.exception.ResourceNotFoundException;
import com.smarttaskmanager.repository.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.*;

@Service
@Transactional
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final SubtaskRepository subtaskRepository;
    private final ActivityLogRepository activityLogRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository,
                      ProjectRepository projectRepository, SubtaskRepository subtaskRepository,
                      ActivityLogRepository activityLogRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.subtaskRepository = subtaskRepository;
        this.activityLogRepository = activityLogRepository;
    }

    public User currentUser(String username) {
        return userRepository.findByUsername(username)
                .or(() -> userRepository.findByEmail(username))
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    /** Accepts canonical + legacy aliases (Pending->TODO, In_Progress->IN_PROGRESS, etc.) */
    public static Task.Status parseStatus(String s) {
        if (s == null || s.isBlank()) return Task.Status.TODO;
        String n = s.trim().toUpperCase().replace(" ", "_").replace("-", "_");
        return switch (n) {
            case "PENDING", "TO_DO", "TO-DO" -> Task.Status.TODO;
            case "INPROGRESS", "IN PROGRESS" -> Task.Status.IN_PROGRESS;
            case "INREVIEW", "IN REVIEW", "REVIEW" -> Task.Status.IN_REVIEW;
            case "DONE", "COMPLETE" -> Task.Status.COMPLETED;
            default -> Task.Status.valueOf(n);
        };
    }

    public static TaskDto toDto(Task t, List<Subtask> subs) {
        TaskDto d = new TaskDto();
        d.setId(t.getId());
        d.setTitle(t.getTitle());
        d.setDescription(t.getDescription());
        d.setPriority(t.getPriority().name());
        d.setStartDate(t.getStartDate());
        d.setDeadline(t.getDeadline());
        d.setStatus(t.getStatus().name());
        d.setCategory(t.getCategory());
        d.setTags(t.getTags());
        d.setEstimatedMinutes(t.getEstimatedMinutes());
        d.setActualMinutes(t.getActualMinutes());
        d.setNotes(t.getNotes());
        if (t.getProject() != null) {
            d.setProjectId(t.getProject().getId());
            d.setProjectName(t.getProject().getName());
        }
        if (subs != null) {
            d.setSubtasks(subs.stream().map(s -> {
                TaskDto.SubtaskDto sd = new TaskDto.SubtaskDto();
                sd.setId(s.getId()); sd.setTitle(s.getTitle()); sd.setDone(s.isDone());
                return sd;
            }).toList());
        }
        return d;
    }

    private void log(User u, Long taskId, String action) {
        ActivityLog l = new ActivityLog();
        l.setUser(u); l.setTaskId(taskId); l.setAction(action);
        activityLogRepository.save(l);
    }

    public List<TaskDto> list(String username) {
        User u = currentUser(username);
        return taskRepository.findByUserIdOrderByCreatedAtDesc(u.getId())
                .stream().map(t -> toDto(t, subtaskRepository.findByTaskId(t.getId()))).toList();
    }

    public TaskDto get(String username, Long id) {
        Task t = owned(username, id);
        return toDto(t, subtaskRepository.findByTaskId(id));
    }

    public TaskDto create(String username, TaskDto dto) {
        User u = currentUser(username);
        Task t = new Task();
        apply(dto, t, u);
        t.setDemo(false);
        Task saved = taskRepository.save(t);
        saveSubtasks(dto, saved);
        log(u, saved.getId(), "CREATED");
        return toDto(saved, subtaskRepository.findByTaskId(saved.getId()));
    }

    public TaskDto duplicate(String username, Long id) {
        User u = currentUser(username);
        Task src = owned(username, id);
        Task t = new Task();
        t.setTitle(src.getTitle() + " (copy)");
        t.setDescription(src.getDescription());
        t.setPriority(src.getPriority());
        t.setStartDate(src.getStartDate());
        t.setDeadline(src.getDeadline());
        t.setStatus(Task.Status.TODO);
        t.setCategory(src.getCategory());
        t.setTags(src.getTags());
        t.setEstimatedMinutes(src.getEstimatedMinutes());
        t.setNotes(src.getNotes());
        t.setUser(u);
        t.setProject(src.getProject());
        Task saved = taskRepository.save(t);
        log(u, saved.getId(), "DUPLICATED");
        return toDto(saved, null);
    }

    public TaskDto update(String username, Long id, TaskDto dto) {
        Task t = owned(username, id);
        User u = currentUser(username);
        boolean wasCompleted = t.getStatus() == Task.Status.COMPLETED;
        apply(dto, t, u);
        Task saved = taskRepository.save(t);
        subtaskRepository.deleteByTaskId(id);
        saveSubtasks(dto, saved);
        if (!wasCompleted && saved.getStatus() == Task.Status.COMPLETED) {
            log(u, id, "COMPLETED"); // single event: only on transition, never on toggle-back
        } else {
            log(u, id, "UPDATED");
        }
        return toDto(saved, subtaskRepository.findByTaskId(id));
    }

    public TaskDto patchStatus(String username, Long id, String status) {
        Task t = owned(username, id);
        User u = currentUser(username);
        boolean wasCompleted = t.getStatus() == Task.Status.COMPLETED;
        t.setStatus(parseStatus(status));
        Task saved = taskRepository.save(t);
        if (!wasCompleted && saved.getStatus() == Task.Status.COMPLETED) log(u, id, "COMPLETED");
        else log(u, id, "STATUS_" + saved.getStatus().name());
        return toDto(saved, subtaskRepository.findByTaskId(id));
    }

    public void delete(String username, Long id) {
        Task t = owned(username, id);
        subtaskRepository.deleteByTaskId(id);
        taskRepository.delete(t);
        log(currentUser(username), id, "DELETED");
    }

    public Task owned(String username, Long id) {
        Task t = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        User u = currentUser(username);
        if (!t.getUser().getId().equals(u.getId()))
            throw new AccessDeniedException("You can only access your own tasks");
        return t;
    }

    private void apply(TaskDto dto, Task t, User u) {
        t.setTitle(dto.getTitle());
        t.setDescription(dto.getDescription());
        t.setPriority(Task.Priority.valueOf(dto.getPriority()));
        t.setStartDate(dto.getStartDate());
        t.setDeadline(dto.getDeadline());
        t.setStatus(parseStatus(dto.getStatus()));
        t.setCategory(dto.getCategory());
        t.setTags(dto.getTags());
        t.setEstimatedMinutes(dto.getEstimatedMinutes());
        if (dto.getActualMinutes() != null) t.setActualMinutes(dto.getActualMinutes());
        t.setNotes(dto.getNotes());
        t.setUser(u);
        if (dto.getProjectId() != null) {
            Project p = projectRepository.findById(dto.getProjectId())
                    .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
            if (!p.getOwner().getId().equals(u.getId())) throw new AccessDeniedException("Not your project");
            t.setProject(p);
        } else {
            t.setProject(null);
        }
    }

    private void saveSubtasks(TaskDto dto, Task saved) {
        if (dto.getSubtasks() == null) return;
        for (TaskDto.SubtaskDto sd : dto.getSubtasks()) {
            if (sd.getTitle() == null || sd.getTitle().isBlank()) continue;
            Subtask s = new Subtask();
            s.setTask(saved); s.setTitle(sd.getTitle()); s.setDone(sd.isDone());
            subtaskRepository.save(s);
        }
    }

    public DashboardSummaryDto summary(String username) {
        User u = currentUser(username);
        long total = taskRepository.countByUserId(u.getId());
        long todo = taskRepository.countByUserIdAndStatus(u.getId(), Task.Status.TODO);
        long prog = taskRepository.countByUserIdAndStatus(u.getId(), Task.Status.IN_PROGRESS);
        long rev = taskRepository.countByUserIdAndStatus(u.getId(), Task.Status.IN_REVIEW);
        long done = taskRepository.countByUserIdAndStatus(u.getId(), Task.Status.COMPLETED);
        long overdue = taskRepository.findByUserIdAndDeadlineBeforeAndStatusNot(
                u.getId(), LocalDate.now(), Task.Status.COMPLETED).size();
        return new DashboardSummaryDto(total, todo, prog, rev, done, overdue);
    }
}
