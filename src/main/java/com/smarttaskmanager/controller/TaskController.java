package com.smarttaskmanager.controller;

import com.smarttaskmanager.dto.TaskDto;
import com.smarttaskmanager.entity.TaskComment;
import com.smarttaskmanager.exception.ResourceNotFoundException;
import com.smarttaskmanager.repository.TaskCommentRepository;
import com.smarttaskmanager.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:3000" })
public class TaskController {
    private final TaskService taskService;
    private final TaskCommentRepository commentRepository;

    public TaskController(TaskService taskService, TaskCommentRepository commentRepository) {
        this.taskService = taskService; this.commentRepository = commentRepository;
    }

    @GetMapping
    public List<TaskDto> list(Authentication auth) {
        return taskService.list(auth.getName());
    }

    @GetMapping("/{id}")
    public TaskDto get(Authentication auth, @PathVariable Long id) {
        return taskService.get(auth.getName(), id);
    }

    @PostMapping
    public ResponseEntity<TaskDto> create(Authentication auth, @Valid @RequestBody TaskDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.create(auth.getName(), dto));
    }

    @PutMapping("/{id}")
    public TaskDto update(Authentication auth, @PathVariable Long id, @Valid @RequestBody TaskDto dto) {
        return taskService.update(auth.getName(), id, dto);
    }

    @PatchMapping("/{id}/status")
    public TaskDto patchStatus(Authentication auth, @PathVariable Long id,
                               @RequestBody Map<String, String> body) {
        return taskService.patchStatus(auth.getName(), id, body.get("status"));
    }

    @PostMapping("/{id}/duplicate")
    public ResponseEntity<TaskDto> duplicate(Authentication auth, @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.duplicate(auth.getName(), id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(Authentication auth, @PathVariable Long id) {
        taskService.delete(auth.getName(), id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/comments")
    public List<Map<String, Object>> comments(Authentication auth, @PathVariable Long id) {
        taskService.owned(auth.getName(), id);
        return commentRepository.findByTaskIdOrderByCreatedAtAsc(id).stream()
                .map(c -> Map.<String, Object>of("id", c.getId(), "text", c.getText(),
                        "author", c.getAuthor().getUsername()))
                .toList();
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<Map<String, Object>> addComment(Authentication auth, @PathVariable Long id,
                                                         @RequestBody Map<String, String> body) {
        var task = taskService.owned(auth.getName(), id);
        TaskComment c = new TaskComment();
        c.setTask(task);
        c.setAuthor(taskService.currentUser(auth.getName()));
        c.setText(body.getOrDefault("text", ""));
        TaskComment saved = commentRepository.save(c);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("id", saved.getId(), "text", saved.getText()));
    }
}
