package com.smarttaskmanager.controller;

import com.smarttaskmanager.dto.RecommendationDto;
import com.smarttaskmanager.service.AssistantService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assistant")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class AssistantController {
    private final AssistantService assistantService;

    public AssistantController(AssistantService assistantService) {
        this.assistantService = assistantService;
    }

    @GetMapping("/daily-plan")
    public RecommendationDto.PlanDto dailyPlan(Authentication auth) {
        return assistantService.dailyPlan(auth.getName());
    }
}
