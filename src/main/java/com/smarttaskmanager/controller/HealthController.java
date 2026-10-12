package com.smarttaskmanager.controller;

import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:3000" })
public class HealthController {

    @GetMapping("/api/health")
    public Map<String, String> health() {
        return Map.of("status", "UP", "service", "smart-task-manager-backend");
    }
}
