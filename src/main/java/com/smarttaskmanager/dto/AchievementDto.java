package com.smarttaskmanager.dto;

public class AchievementDto {
    private String code;
    private String name;
    private String description;
    private String icon;
    private int xp;
    private boolean earned;

    public AchievementDto(String code, String name, String description, String icon, int xp, boolean earned) {
        this.code = code; this.name = name; this.description = description;
        this.icon = icon; this.xp = xp; this.earned = earned;
    }
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getIcon() { return icon; }
    public int getXp() { return xp; }
    public boolean isEarned() { return earned; }
}
