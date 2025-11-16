package com.ma.torneos.domain;

public class School {
    private Long id;
    private Long ownerUserId;
    private String name;
    private String city;
    private String contactPhone;

    public School(Long id, Long ownerUserId, String name, String city, String contactPhone) {
        this.id = id;
        this.ownerUserId = ownerUserId;
        this.name = name;
        this.city = city;
        this.contactPhone = contactPhone;
    }

    public Long getId() { return id; }
    public Long getOwnerUserId() { return ownerUserId; }
    public String getName() { return name; }
    public String getCity() { return city; }
    public String getContactPhone() { return contactPhone; }

    @Override
    public String toString() {
        return name + " (" + city + ")";
    }
}
