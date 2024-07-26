package com.example.rqchallenge.employees.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SingleEmployeeResponse {
    private String status;
    private Employee data;

    public String getStatus() {
        return status;
    }

    public Employee getData() {
        return data;
    }
}
