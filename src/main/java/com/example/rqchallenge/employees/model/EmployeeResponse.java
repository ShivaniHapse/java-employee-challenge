package com.example.rqchallenge.employees.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EmployeeResponse {
    private String status;
    private List<Employee> data;

    public String getStatus() {
        return status;
    }

    public List<Employee> getData() {
        return data;
    }
}
