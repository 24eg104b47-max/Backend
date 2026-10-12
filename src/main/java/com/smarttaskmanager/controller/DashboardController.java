package com.smarttaskmanager.controller;

import com.smarttaskmanager.dto.DashboardSummaryDto;
import com.smarttaskmanager.dto.ProductivityDto;
import com.smarttaskmanager.service.ProductivityService;
import com.smarttaskmanager.service.TaskService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class DashboardController {
    private final TaskService taskService;
    private final ProductivityService productivityService;

    public DashboardController(TaskService taskService, ProductivityService productivityService) {
        this.taskService = taskService; this.productivityService = productivityService;
    }

    @GetMapping("/summary")
    public DashboardSummaryDto summary(Authentication auth) {
        return taskService.summary(auth.getName());
    }

    @GetMapping("/productivity")
    public ProductivityDto productivity(Authentication auth) {
        return productivityService.productivity(auth.getName());
    }
}
