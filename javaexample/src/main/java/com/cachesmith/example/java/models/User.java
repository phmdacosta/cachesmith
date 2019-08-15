package com.cachesmith.example.java.models;

import com.cachesmith.library.annotations.AutoIncrement;
import com.cachesmith.library.annotations.Column;
import com.cachesmith.library.annotations.PrimaryKey;
import com.cachesmith.library.annotations.Relationship;
import com.cachesmith.library.annotations.Table;
import com.cachesmith.library.annotations.Unique;
import com.cachesmith.library.util.DataType;
import com.cachesmith.library.util.RelationType;

import java.util.List;

@Table(name = "USER")
public class User {
    @PrimaryKey
    @AutoIncrement
    @Unique
    private long id;

    private String name;
    private int age;
    private String gender;

    @Column(name = "PROFESSION", type = DataType.TEXT)
    private String prof;

    private boolean active;

    @Relationship(type = RelationType.ONE_TO_MANY)
    private List<Address> adresses;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getProf() {
        return prof;
    }

    public void setProf(String prof) {
        this.prof = prof;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<Address> getAdresses() {
        return adresses;
    }

    public void setAdresses(List<Address> adresses) {
        this.adresses = adresses;
    }
}
