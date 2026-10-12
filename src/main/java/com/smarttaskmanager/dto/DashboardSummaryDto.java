package com.smarttaskmanager.dto;

public class DashboardSummaryDto {
    private long total;
    private long todo;
    private long inProgress;
    private long inReview;
    private long completed;
    private long overdue;
    private double completionRate;

    // Backwards-compatible aliases for the v1 frontend
    public long getPending() { return todo; }
    public void setPending(long v) { this.todo = v; }

    public DashboardSummaryDto(long total, long todo, long inProgress, long inReview,
                               long completed, long overdue) {
        this.total = total; this.todo = todo; this.inProgress = inProgress;
        this.inReview = inReview; this.completed = completed; this.overdue = overdue;
        this.completionRate = total == 0 ? 0 : Math.round(completed * 1000.0 / total) / 10.0;
    }
    public long getTotal() { return total; }
    public long getTodo() { return todo; }
    public long getInProgress() { return inProgress; }
    public long getInReview() { return inReview; }
    public long getCompleted() { return completed; }
    public long getOverdue() { return overdue; }
    public double getCompletionRate() { return completionRate; }
}
