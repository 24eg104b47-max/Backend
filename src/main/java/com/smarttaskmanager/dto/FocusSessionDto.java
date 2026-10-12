package com.smarttaskmanager.dto;

public class FocusSessionDto {
    private Long id;
    private Long taskId;
    private String kind = "FOCUS";
    private int minutesPlanned = 25;
    private int minutesActual = 25;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getTaskId() { return taskId; }
    public void setTaskId(Long v) { this.taskId = v; }
    public String getKind() { return kind; }
    public void setKind(String k) { this.kind = k; }
    public int getMinutesPlanned() { return minutesPlanned; }
    public void setMinutesPlanned(int v) { this.minutesPlanned = v; }
    public int getMinutesActual() { return minutesActual; }
    public void setMinutesActual(int v) { this.minutesActual = v; }
}
