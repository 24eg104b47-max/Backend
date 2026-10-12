package com.smarttaskmanager.controller;

import com.smarttaskmanager.dto.AchievementDto;
import com.smarttaskmanager.service.AchievementService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/achievements")
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:3000" })
public class AchievementController {
    private final AchievementService achievementService;

    public AchievementController(AchievementService achievementService) {
        this.achievementService = achievementService;
    }

    @GetMapping
    public Map<String, Object> achievements(Authentication auth) {
        List<AchievementDto> all = achievementService.evaluate(auth.getName());
        int xp = achievementService.totalXp(auth.getName());
        int level = xp / 50 + 1;
        return Map.of("achievements", all, "xp", xp, "level", level);
    }
}
