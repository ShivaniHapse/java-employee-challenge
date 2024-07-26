package com.example.rqchallenge.employees;

import com.example.rqchallenge.employees.entity.Employee;

import java.util.List;

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
