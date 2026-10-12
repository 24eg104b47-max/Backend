package com.smarttaskmanager.service;

import com.smarttaskmanager.dto.FocusSessionDto;
import com.smarttaskmanager.entity.FocusSession;
import com.smarttaskmanager.entity.User;
import com.smarttaskmanager.repository.FocusSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class FocusService {
    private final FocusSessionRepository repo;
    private final TaskService taskService;

    public FocusService(FocusSessionRepository repo, TaskService taskService) {
        this.repo = repo; this.taskService = taskService;
    }

    public List<FocusSessionDto> list(String username) {
        User u = taskService.currentUser(username);
        return repo.findByUserIdOrderByCompletedAtDesc(u.getId()).stream().map(s -> {
            FocusSessionDto d = new FocusSessionDto();
            d.setId(s.getId()); d.setTaskId(s.getTaskId()); d.setKind(s.getKind().name());
            d.setMinutesPlanned(s.getMinutesPlanned()); d.setMinutesActual(s.getMinutesActual());
            return d;
        }).toList();
    }

    public FocusSessionDto record(String username, FocusSessionDto dto) {
        User u = taskService.currentUser(username);
        FocusSession s = new FocusSession();
        s.setUser(u);
        s.setTaskId(dto.getTaskId());
        try { s.setKind(FocusSession.Kind.valueOf(dto.getKind())); }
        catch (Exception e) { s.setKind(FocusSession.Kind.FOCUS); }
        s.setMinutesPlanned(dto.getMinutesPlanned());
        s.setMinutesActual(dto.getMinutesActual());
        FocusSession saved = repo.save(s);
        // accumulate actual focus time on the linked task
        if (saved.getTaskId() != null && saved.getKind() == FocusSession.Kind.FOCUS) {
            try {
                var t = taskService.owned(username, saved.getTaskId());
                t.setActualMinutes((t.getActualMinutes() == null ? 0 : t.getActualMinutes())
                        + saved.getMinutesActual());
            } catch (Exception ignored) { /* task may be gone */ }
        }
        dto.setId(saved.getId());
        return dto;
    }
}
