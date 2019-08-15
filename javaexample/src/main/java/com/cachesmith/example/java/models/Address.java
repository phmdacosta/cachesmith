package com.cachesmith.example.java.models;

import com.cachesmith.library.annotations.Relationship;
import com.cachesmith.library.util.RelationType;

public class Address {
    private String description;
    private String zipcode;
    private String city;
    private String contry;

    @Relationship(type = RelationType.MANY_TO_ONE)
    private User user;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getContry() {
        return contry;
    }

    public void setContry(String contry) {
        this.contry = contry;
    }

    public User getUser() { return user; }

    public void setUser(User user) { this.user = user; }
}
