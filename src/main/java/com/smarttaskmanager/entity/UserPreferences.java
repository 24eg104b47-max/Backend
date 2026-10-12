package com.smarttaskmanager.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "user_preferences")
public class UserPreferences {
    @Id
    private Long userId;

    @Column(length = 10)
    private String theme = "dark";

    @Column(length = 20)
    private String accent = "#38bdf8";

    @Column(name = "focus_minutes")
    private int focusMinutes = 25;

    @Column(name = "short_break_minutes")
    private int shortBreakMinutes = 5;

    @Column(name = "long_break_minutes")
    private int longBreakMinutes = 15;

    @Column(name = "notify_overdue")
    private boolean notifyOverdue = true;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getTheme() { return theme; }
    public void setTheme(String theme) { this.theme = theme; }
    public String getAccent() { return accent; }
    public void setAccent(String accent) { this.accent = accent; }
    public int getFocusMinutes() { return focusMinutes; }
    public void setFocusMinutes(int v) { this.focusMinutes = v; }
    public int getShortBreakMinutes() { return shortBreakMinutes; }
    public void setShortBreakMinutes(int v) { this.shortBreakMinutes = v; }
    public int getLongBreakMinutes() { return longBreakMinutes; }
    public void setLongBreakMinutes(int v) { this.longBreakMinutes = v; }
    public boolean isNotifyOverdue() { return notifyOverdue; }
    public void setNotifyOverdue(boolean v) { this.notifyOverdue = v; }
}
