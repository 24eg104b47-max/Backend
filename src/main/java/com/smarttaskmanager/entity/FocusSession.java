package com.smarttaskmanager.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "focus_sessions")
public class FocusSession {
    public enum Kind { FOCUS, SHORT_BREAK, LONG_BREAK }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "task_id")
    private Long taskId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Kind kind = Kind.FOCUS;

    @Column(name = "minutes_planned", nullable = false)
    private int minutesPlanned = 25;

    @Column(name = "minutes_actual", nullable = false)
    private int minutesActual = 25;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @PrePersist
    protected void onCreate() { if (completedAt == null) completedAt = LocalDateTime.now(); }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }
    public Kind getKind() { return kind; }
    public void setKind(Kind kind) { this.kind = kind; }
    public int getMinutesPlanned() { return minutesPlanned; }
    public void setMinutesPlanned(int v) { this.minutesPlanned = v; }
    public int getMinutesActual() { return minutesActual; }
    public void setMinutesActual(int v) { this.minutesActual = v; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime v) { this.completedAt = v; }
}
