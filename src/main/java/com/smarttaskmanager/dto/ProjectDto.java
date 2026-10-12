package com.smarttaskmanager.dto;

import jakarta.validation.constraints.NotBlank;

public class ProjectDto {
    private Long id;
    @NotBlank
    private String name;
    private String description;
    private String color;
    private String icon;
    private long totalTasks;
    private long completedTasks;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String d) { this.description = d; }
    public String getColor() { return color; }
    public void setColor(String c) { this.color = c; }
    public String getIcon() { return icon; }
    public void setIcon(String i) { this.icon = i; }
    public long getTotalTasks() { return totalTasks; }
    public void setTotalTasks(long v) { this.totalTasks = v; }
    public long getCompletedTasks() { return completedTasks; }
    public void setCompletedTasks(long v) { this.completedTasks = v; }
}
