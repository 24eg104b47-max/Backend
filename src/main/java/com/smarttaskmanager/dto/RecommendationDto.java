package com.smarttaskmanager.dto;

import java.util.List;

public class RecommendationDto {
    private String type;
    private String title;
    private String reason;
    private Long taskId;

    public RecommendationDto(String type, String title, String reason, Long taskId) {
        this.type = type; this.title = title; this.reason = reason; this.taskId = taskId;
    }
    public String getType() { return type; }
    public String getTitle() { return title; }
    public String getReason() { return reason; }
    public Long getTaskId() { return taskId; }

    public static class PlanDto {
        private List<RecommendationDto> recommendations;
        private String summary;
        public PlanDto(List<RecommendationDto> r, String s) { this.recommendations = r; this.summary = s; }
        public List<RecommendationDto> getRecommendations() { return recommendations; }
        public String getSummary() { return summary; }
    }
}
