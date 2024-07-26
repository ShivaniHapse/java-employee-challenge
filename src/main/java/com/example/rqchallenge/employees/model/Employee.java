package com.example.rqchallenge.employees.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Employee {

    private int id;

    @JsonProperty("employee_name")
    private String name;

    @JsonProperty("employee_age")
    private int age;

    @JsonProperty("employee_salary")
    private int salary;

    @JsonProperty("profile_image")
    private String profileImage;

    public Employee(int id, String name, int salary, int age) {
        this.id = id;
        this.name = name;
        this.salary = salary;
        this.age = age;
    }

    public Employee() {
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public int getSalary() {
        return salary;
    }

    public String getProfileImage() {
        return profileImage;
    }
}
