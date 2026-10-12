package com.smarttaskmanager.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "achievements")
public class Achievement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 60)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 255)
    private String description;

    @Column(length = 10)
    private String icon = "🏆";

    @Column(nullable = false)
    private int xp = 10;

    public Achievement() {}
    public Achievement(String code, String name, String description, String icon, int xp) {
        this.code = code; this.name = name; this.description = description;
        this.icon = icon; this.xp = xp;
    }

    public Long getId() { return id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public void setDescription(String d) { this.description = d; }
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    public int getXp() { return xp; }
    public void setXp(int xp) { this.xp = xp; }
}
