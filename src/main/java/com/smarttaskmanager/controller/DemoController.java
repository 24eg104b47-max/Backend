package com.smarttaskmanager.controller;

import com.smarttaskmanager.service.DemoService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/demo")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class DemoController {
    private final DemoService demoService;

    public DemoController(DemoService demoService) {
        this.demoService = demoService;
    }

    @PostMapping("/seed")
    public Map<String, Object> seed(Authentication auth) {
        int n = demoService.seed(auth.getName());
        return Map.of("seeded", n, "message",
                n == 0 ? "Demo data already present." : n + " demo tasks created.");
    }

    @PostMapping("/reset")
    public Map<String, Object> reset(Authentication auth) {
        int n = demoService.reset(auth.getName());
        return Map.of("deleted", n, "message", "Removed " + n + " demo record(s). Real tasks untouched.");
    }
}
