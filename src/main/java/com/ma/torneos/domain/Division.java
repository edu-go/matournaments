package com.ma.torneos.domain;

public class Division {
    private Long id;
    private Long tournamentId;
    private String name;
    private int ageMin;
    private int ageMax;
    private double weightMin;
    private double weightMax;
    private String beltMin;
    private String beltMax;
    private String gender; // "M", "F", "X"

    public Division(Long id, Long tournamentId, String name,
                    int ageMin, int ageMax,
                    double weightMin, double weightMax,
                    String beltMin, String beltMax,
                    String gender) {
        this.id = id;
        this.tournamentId = tournamentId;
        this.name = name;
        this.ageMin = ageMin;
        this.ageMax = ageMax;
        this.weightMin = weightMin;
        this.weightMax = weightMax;
        this.beltMin = beltMin;
        this.beltMax = beltMax;
        this.gender = gender;
    }

    public Long getId() { return id; }
    public Long getTournamentId() { return tournamentId; }
    public String getName() { return name; }
    public int getAgeMin() { return ageMin; }
    public int getAgeMax() { return ageMax; }
    public double getWeightMin() { return weightMin; }
    public double getWeightMax() { return weightMax; }
    public String getBeltMin() { return beltMin; }
    public String getBeltMax() { return beltMax; }
    public String getGender() { return gender; }

    public void setId(Long id) { this.id = id; }
    public void setTournamentId(Long tournamentId) { this.tournamentId = tournamentId; }
    public void setName(String name) { this.name = name; }
    public void setAgeMin(int ageMin) { this.ageMin = ageMin; }
    public void setAgeMax(int ageMax) { this.ageMax = ageMax; }
    public void setWeightMin(double weightMin) { this.weightMin = weightMin; }
    public void setWeightMax(double weightMax) { this.weightMax = weightMax; }
    public void setBeltMin(String beltMin) { this.beltMin = beltMin; }
    public void setBeltMax(String beltMax) { this.beltMax = beltMax; }
    public void setGender(String gender) { this.gender = gender; }
}
