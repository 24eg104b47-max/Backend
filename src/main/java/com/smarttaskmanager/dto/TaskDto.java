package com.smarttaskmanager.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

public class TaskDto {
    private Long id;
    @NotBlank(message = "Task title is required")
    @Size(max = 150)
    private String title;
    private String description;
    @Pattern(regexp = "Critical|High|Medium|Low", message = "Priority must be Critical, High, Medium or Low")
    private String priority = "Medium";
    private LocalDate startDate;
    private LocalDate deadline;
    private String status = "TODO";
    private String category;
    private String tags;
    private Integer estimatedMinutes;
    private Integer actualMinutes;
    private String notes;
    private Long projectId;
    private String projectName;
    private List<SubtaskDto> subtasks;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate v) { this.startDate = v; }
    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCategory() { return category; }
    public void setCategory(String v) { this.category = v; }
    public String getTags() { return tags; }
    public void setTags(String v) { this.tags = v; }
    public Integer getEstimatedMinutes() { return estimatedMinutes; }
    public void setEstimatedMinutes(Integer v) { this.estimatedMinutes = v; }
    public Integer getActualMinutes() { return actualMinutes; }
    public void setActualMinutes(Integer v) { this.actualMinutes = v; }
    public String getNotes() { return notes; }
    public void setNotes(String v) { this.notes = v; }
    public Long getProjectId() { return projectId; }
    public void setProjectId(Long v) { this.projectId = v; }
    public String getProjectName() { return projectName; }
    public void setProjectName(String v) { this.projectName = v; }
    public List<SubtaskDto> getSubtasks() { return subtasks; }
    public void setSubtasks(List<SubtaskDto> v) { this.subtasks = v; }

    public static class SubtaskDto {
        private Long id;
        private String title;
        private boolean done;
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String t) { this.title = t; }
        public boolean isDone() { return done; }
        public void setDone(boolean d) { this.done = d; }
    }
}
