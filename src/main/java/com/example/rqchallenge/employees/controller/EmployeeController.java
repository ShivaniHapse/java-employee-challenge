package com.example.rqchallenge.employees.controller;

import com.example.rqchallenge.employees.model.Employee;
import com.example.rqchallenge.employees.service.EmployeeService;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;

@RestController
@RequestMapping("/employees")
public class EmployeeController implements IEmployeeController {

    private final EmployeeService employeeService;
    private static final Logger logger = getLogger(EmployeeController.class);

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Override
    public ResponseEntity<List<Employee>> getAllEmployees() {
        logger.info("Received request to get all employees");
        try {
            List<Employee> employees = employeeService.getAllEmployees();
            return ok(employees);
        } catch (RuntimeException e) {
            logger.error("Failed to get all employees", e);
            return status(INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @Override
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(String searchString) {
        logger.info("Received request to search for employees with name containing: {}", searchString);
        try {
            List<Employee> employees = employeeService.getEmployeesByNameSearch(searchString);
            return ok(employees);
        } catch (RuntimeException e) {
            logger.error("Failed to search for employees", e);
            return status(INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Override
    public ResponseEntity<Employee> getEmployeeById(String id) {
        logger.info("Received request to get employee by id: {}", id);
        try {
            Employee employee = employeeService.getEmployeeById(id);
            return ok(employee);
        } catch (RuntimeException e) {
            logger.error("Failed to get employee by id: {}", id, e);
            return status(INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Override
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        logger.info("Received request to get highest salary of employees");
        try {
            Integer highestSalary = employeeService.getHighestSalaryOfEmployees();
            return ok(highestSalary);
        } catch (RuntimeException e) {
            logger.error("Failed to get highest salary of employees", e);
            return status(INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Override
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        logger.info("Received request to get top 10 highest earning employee names");
        try {
            List<String> topTenNames = employeeService.getTopTenHighestEarningEmployeeNames();
            return ok(topTenNames);
        } catch (RuntimeException e) {
            logger.error("Failed to get top 10 highest earning employee names", e);
            return status(INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Override
    public ResponseEntity<Employee> createEmployee(Map<String, Object> employeeInput) {
        logger.info("Received request to create employee with {}", employeeInput);
        try {
            Employee employee = employeeService.createEmployee(employeeInput);
            return status(CREATED).body(employee);
        } catch (RuntimeException e) {
            logger.error("Failed to create employee", e);
            return status(INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Override
    public ResponseEntity<String> deleteEmployeeById(String id) {
        logger.info("Received request to delete employee with id: {}", id);
        try {
            String employeeName = employeeService.deleteEmployee(id);
            return ok("Deleted employee: " + employeeName);
        } catch (RuntimeException e) {
            logger.error("Failed to delete employee with id: {}", id, e);
            return status(INTERNAL_SERVER_ERROR).body("Failed to delete employee");
        }
    }

    @Override
    public ResponseEntity<String> deleteEmployeeByIdAndReturnStatus(String id) {
        logger.info("Received request to delete employee with id: {}", id);
        try {
            String employeeName = employeeService.deleteEmployeeAndReturnStatus(id);
            return ok("Employee deletion status : " + employeeName);
        } catch (RuntimeException e) {
            logger.error("Failed to delete employee with id: {}", id, e);
            return status(INTERNAL_SERVER_ERROR).body("Failed to delete employee");
        }
    }
}
