package com.smarttaskmanager.controller;

import com.smarttaskmanager.dto.FocusSessionDto;
import com.smarttaskmanager.service.FocusService;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/focus-sessions")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class FocusController {
    private final FocusService focusService;

    public FocusController(FocusService focusService) {
        this.focusService = focusService;
    }

    @GetMapping
    public List<FocusSessionDto> list(Authentication auth) {
        return focusService.list(auth.getName());
    }

    @PostMapping
    public ResponseEntity<FocusSessionDto> record(Authentication auth, @RequestBody FocusSessionDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(focusService.record(auth.getName(), dto));
    }
}
