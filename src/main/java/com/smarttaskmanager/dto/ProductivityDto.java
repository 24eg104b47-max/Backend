package com.smarttaskmanager.dto;

import java.util.List;
import java.util.Map;

public class ProductivityDto {
    private List<Map<String, Object>> weeklyCompletion;
    private Map<String, Long> priorityDistribution;
    private Map<String, Long> statusDistribution;
    private long overdue;
    private long dueToday;
    private long focusMinutesToday;

    public ProductivityDto(List<Map<String, Object>> weeklyCompletion,
                           Map<String, Long> priorityDistribution,
                           Map<String, Long> statusDistribution,
                           long overdue, long dueToday, long focusMinutesToday) {
        this.weeklyCompletion = weeklyCompletion;
        this.priorityDistribution = priorityDistribution;
        this.statusDistribution = statusDistribution;
        this.overdue = overdue;
        this.dueToday = dueToday;
        this.focusMinutesToday = focusMinutesToday;
    }
    public List<Map<String, Object>> getWeeklyCompletion() { return weeklyCompletion; }
    public Map<String, Long> getPriorityDistribution() { return priorityDistribution; }
    public Map<String, Long> getStatusDistribution() { return statusDistribution; }
    public long getOverdue() { return overdue; }
    public long getDueToday() { return dueToday; }
    public long getFocusMinutesToday() { return focusMinutesToday; }
}
