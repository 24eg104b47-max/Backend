package com.smarttaskmanager.dto;

public class NotificationDto {
    private Long id;
    private String title;
    private String message;
    private String kind;
    private boolean read;

    public NotificationDto(Long id, String title, String message, String kind, boolean read) {
        this.id = id; this.title = title; this.message = message;
        this.kind = kind; this.read = read;
    }
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getKind() { return kind; }
    public boolean isRead() { return read; }
}
