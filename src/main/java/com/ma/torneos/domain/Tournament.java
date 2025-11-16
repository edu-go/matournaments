package com.ma.torneos.domain;

import java.time.LocalDate;

public class Tournament {
    private Long id;
    private String name;
    private LocalDate startDate;
    private String location;
    private String ruleset;
    private String status; // DRAFT, OPEN, CLOSED, ARCHIVED

    public Tournament(Long id, String name, LocalDate startDate,
                      String location, String ruleset, String status) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.location = location;
        this.ruleset = ruleset;
        this.status = status;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public LocalDate getStartDate() { return startDate; }
    public String getLocation() { return location; }
    public String getRuleset() { return ruleset; }
    public String getStatus() { return status; }

    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public void setLocation(String location) { this.location = location; }
    public void setRuleset(String ruleset) { this.ruleset = ruleset; }
    public void setStatus(String status) { this.status = status; }
}