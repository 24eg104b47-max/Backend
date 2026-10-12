package com.smarttaskmanager.controller;

import com.smarttaskmanager.entity.UserPreferences;
import com.smarttaskmanager.repository.UserPreferencesRepository;
import com.smarttaskmanager.service.TaskService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/preferences")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class PreferencesController {
    private final UserPreferencesRepository repo;
    private final TaskService taskService;

    public PreferencesController(UserPreferencesRepository repo, TaskService taskService) {
        this.repo = repo; this.taskService = taskService;
    }

    @GetMapping
    public UserPreferences get(Authentication auth) {
        Long uid = taskService.currentUser(auth.getName()).getId();
        return repo.findById(uid).orElseGet(() -> {
            UserPreferences p = new UserPreferences();
            p.setUserId(uid);
            return repo.save(p);
        });
    }

    @PutMapping
    public UserPreferences save(Authentication auth, @RequestBody UserPreferences prefs) {
        Long uid = taskService.currentUser(auth.getName()).getId();
        prefs.setUserId(uid);
        return repo.save(prefs);
    }
}
