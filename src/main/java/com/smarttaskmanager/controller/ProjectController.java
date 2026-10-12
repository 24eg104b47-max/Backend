package com.smarttaskmanager.controller;

import com.smarttaskmanager.dto.ProjectDto;
import com.smarttaskmanager.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class ProjectController {
    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public List<ProjectDto> list(Authentication auth) {
        return projectService.list(auth.getName());
    }

    @PostMapping
    public ResponseEntity<ProjectDto> create(Authentication auth, @Valid @RequestBody ProjectDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.create(auth.getName(), dto));
    }

    @PutMapping("/{id}")
    public ProjectDto update(Authentication auth, @PathVariable Long id, @Valid @RequestBody ProjectDto dto) {
        return projectService.update(auth.getName(), id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(Authentication auth, @PathVariable Long id) {
        projectService.delete(auth.getName(), id);
        return ResponseEntity.noContent().build();
    }
}
