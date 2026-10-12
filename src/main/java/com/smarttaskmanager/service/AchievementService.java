package com.smarttaskmanager.service;

import com.smarttaskmanager.dto.AchievementDto;
import com.smarttaskmanager.entity.*;
import com.smarttaskmanager.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.*;

@Service
@Transactional
public class AchievementService {
    private final AchievementRepository achievementRepo;
    private final UserAchievementRepository userAchievementRepo;
    private final ActivityLogRepository activityLogRepository;
    private final FocusSessionRepository focusSessionRepository;
    private final TaskService taskService;

    public AchievementService(AchievementRepository achievementRepo,
                              UserAchievementRepository userAchievementRepo,
                              ActivityLogRepository activityLogRepository,
                              FocusSessionRepository focusSessionRepository,
                              TaskService taskService) {
        this.achievementRepo = achievementRepo;
        this.userAchievementRepo = userAchievementRepo;
        this.activityLogRepository = activityLogRepository;
        this.focusSessionRepository = focusSessionRepository;
        this.taskService = taskService;
    }

    private static final List<Achievement> CATALOG = List.of(
            new Achievement("FIRST_DONE", "First Task Completed", "Complete your first task", "🌱", 10),
            new Achievement("FIVE_DONE", "Five Tasks Completed", "Complete 5 tasks", "⭐", 25),
            new Achievement("TEN_DONE", "Double Digits", "Complete 10 tasks", "🚀", 50),
            new Achievement("FOCUS_STARTER", "Focus Session Champion", "Finish a 25+ min focus session", "🎯", 20),
            new Achievement("FOCUS_MARATHON", "Deep Worker", "Accumulate 120 focus minutes", "🔥", 40),
            new Achievement("STREAK_3", "Consistent Planner", "Complete tasks on 3 different days", "📅", 30)
    );

    private void ensureCatalog() {
        for (Achievement a : CATALOG) {
            if (achievementRepo.findByCode(a.getCode()).isEmpty()) achievementRepo.save(
                    new Achievement(a.getCode(), a.getName(), a.getDescription(), a.getIcon(), a.getXp()));
        }
    }

    private void grant(User u, String code) {
        if (userAchievementRepo.existsByUserIdAndAchievementCode(u.getId(), code)) return;
        achievementRepo.findByCode(code).ifPresent(a -> {
            UserAchievement ua = new UserAchievement();
            ua.setUser(u); ua.setAchievement(a);
            userAchievementRepo.save(ua);
        });
    }

    /** Recomputes from stored COMPLETED events only (no toggle exploit: counts unique task ids). */
    public List<AchievementDto> evaluate(String username) {
        ensureCatalog();
        User u = taskService.currentUser(username);
        long completions = activityLogRepository.countByUserIdAndAction(u.getId(), "COMPLETED");
        if (completions >= 1) grant(u, "FIRST_DONE");
        if (completions >= 5) grant(u, "FIVE_DONE");
        if (completions >= 10) grant(u, "TEN_DONE");
        long focusMin = focusSessionRepository.findByUserIdOrderByCompletedAtDesc(u.getId())
                .stream().filter(s -> s.getKind() == FocusSession.Kind.FOCUS)
                .mapToLong(FocusSession::getMinutesActual).sum();
        boolean has25 = focusSessionRepository.findByUserIdOrderByCompletedAtDesc(u.getId())
                .stream().anyMatch(s -> s.getKind() == FocusSession.Kind.FOCUS && s.getMinutesActual() >= 25);
        if (has25) grant(u, "FOCUS_STARTER");
        if (focusMin >= 120) grant(u, "FOCUS_MARATHON");
        // streak: active days with a COMPLETED event in last 7 days
        long activeDays = 0;
        for (int i = 0; i < 7; i++) {
            LocalDate day = LocalDate.now().minusDays(i);
            long a = activityLogRepository.countByUserIdAndActionAndCreatedAtAfter(
                    u.getId(), "COMPLETED", day.atStartOfDay().minusSeconds(1));
            long b = activityLogRepository.countByUserIdAndActionAndCreatedAtAfter(
                    u.getId(), "COMPLETED", day.plusDays(1).atStartOfDay().minusSeconds(1));
            if (a - b > 0) activeDays++;
        }
        if (activeDays >= 3) grant(u, "STREAK_3");

        Set<String> earned = new HashSet<>();
        userAchievementRepo.findByUserId(u.getId())
                .forEach(ua -> earned.add(ua.getAchievement().getCode()));
        return achievementRepo.findAll().stream()
                .map(a -> new AchievementDto(a.getCode(), a.getName(), a.getDescription(),
                        a.getIcon(), a.getXp(), earned.contains(a.getCode())))
                .toList();
    }

    public int totalXp(String username) {
        User u = taskService.currentUser(username);
        return userAchievementRepo.findByUserId(u.getId()).stream()
                .mapToInt(ua -> ua.getAchievement().getXp()).sum();
    }
}
