package com.example.rqchallenge.employees.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Employee {

    private int id;

    @JsonProperty("employee_name")
    private String name;

    @JsonProperty("employee_age")
    private String age;

    @JsonProperty("employee_salary")
    private int salary;

    @JsonProperty("profile_image")
    private String profileImage;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAge() {
        return age;
    }

    public int getSalary() {
        return salary;
    }

    public String getProfileImage() {
        return profileImage;
    }
}
