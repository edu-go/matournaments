package com.ma.torneos.domain;

import java.time.LocalDate;

public class Competitor {
    private Long id;
    private Long schoolId;
    private String firstName;
    private String lastName;
    private LocalDate birthdate;
    private String gender;
    private String beltLevel;
    private double weightKg;

    public Competitor(Long id, Long schoolId, String firstName, String lastName,
                      LocalDate birthdate, String gender, String beltLevel, double weightKg) {
        this.id = id;
        this.schoolId = schoolId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthdate = birthdate;
        this.gender = gender;
        this.beltLevel = beltLevel;
        this.weightKg = weightKg;
    }

    public Long getId() { return id; }
    public Long getSchoolId() { return schoolId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public LocalDate getBirthdate() { return birthdate; }
    public String getGender() { return gender; }
    public String getBeltLevel() { return beltLevel; }
    public double getWeightKg() { return weightKg; }

    public String getFullName() {
        return lastName + ", " + firstName;
    }
}
