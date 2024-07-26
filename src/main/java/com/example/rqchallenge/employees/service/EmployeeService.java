package com.example.rqchallenge.employees.service;

import com.example.rqchallenge.employees.model.EmployeeResponse;
import com.example.rqchallenge.employees.model.Employee;
import com.example.rqchallenge.employees.model.EmployeeCreated;
import com.example.rqchallenge.employees.model.EmployeeCreationResponse;
import com.example.rqchallenge.employees.model.SingleEmployeeResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static java.util.Comparator.*;
import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpMethod.DELETE;

@Service
public class EmployeeService {

    private static final String BASE_URL = "https://dummy.restapiexample.com/api/v1/";
    public static final String SUCCESS = "success";
    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);
    private final RestTemplate restTemplate;

    @Autowired
    public EmployeeService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Employee> getAllEmployees() {
        String url = BASE_URL + "employees";
        try {
            EmployeeResponse employeeResponse = restTemplate.getForObject(url, EmployeeResponse.class);
            return Optional.ofNullable(employeeResponse)
                    .stream()
                    .filter(response -> response.getStatus().equals(SUCCESS))
                    .map(EmployeeResponse::getData)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Error fetching employees"));
        } catch (HttpClientErrorException e) {
            logger.error("Error fetching employees", e);
            throw new RuntimeException("Error fetching employees", e);
        }
    }

    public List<Employee> getEmployeesByNameSearch(String searchString) {
        logger.info("Searching for employees with name containing: {}", searchString);
        List<Employee> allEmployees = getAllEmployees();
        return allEmployees.stream()
                .filter(Objects::nonNull)
                .filter(employee -> Objects.nonNull(employee.getName()))
                .filter(employee -> employee.getName().toLowerCase().contains(searchString.toLowerCase()))
                .collect(toList());
    }

    public Employee getEmployeeById(String id) {
        if (Objects.isNull(id)) {
            throw new RuntimeException("NonNull Id required");
        }
        logger.info("Fetching employee by id: {}", id);
        String url = BASE_URL + "employee/" + id;
        try {
            SingleEmployeeResponse employeeResponse = restTemplate.getForObject(url, SingleEmployeeResponse.class);
            return Optional.ofNullable(employeeResponse)
                    .stream()
                    .filter(response -> response.getStatus().equals(SUCCESS))
                    .map(SingleEmployeeResponse::getData)
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Error fetching employee by id"));
        } catch (HttpClientErrorException e) {
            logger.error("Error fetching employee by id: {}", id, e);
            throw new RuntimeException("Error fetching employee by id", e);
        }
    }

    public Integer getHighestSalaryOfEmployees() {
        logger.info("Fetching the highest salary among employees");
        List<Employee> allEmployees = getAllEmployees();
        return allEmployees.stream()
                .map(Employee::getSalary)
                .max(naturalOrder())
                .orElse(0);
    }

    public List<String> getTopTenHighestEarningEmployeeNames() {
        logger.info("Fetching the top 10 highest earning employee names");
        List<Employee> allEmployees = getAllEmployees();
        return allEmployees.stream()
                .sorted(comparing(Employee::getSalary, reverseOrder()))
                .limit(10)
                .map(Employee::getName)
                .collect(toList());
    }

    public Employee createEmployee(Map<String, Object> employeeInput) {
        logger.info("Creating employee {}", employeeInput);
        EmployeeCreationResponse employeeCreationResponse;
        try {
            String url = BASE_URL + "create";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(employeeInput, headers);
            employeeCreationResponse = restTemplate.postForObject(url, requestEntity, EmployeeCreationResponse.class);
        } catch (HttpClientErrorException e) {
            logger.error("Error creating employee", e);
            throw new RuntimeException("Error creating employee");
        }
        if (Objects.nonNull(employeeCreationResponse) && SUCCESS.equals(employeeCreationResponse.getStatus())) {
            EmployeeCreated data = employeeCreationResponse.getData();
            return new Employee(data.getId(), data.getName(), data.getSalary(), data.getAge());
        } else {
            logger.error("Error creating employee");
            throw new RuntimeException("Error creating employee");
        }
    }

    public String deleteEmployee(String id) {
        logger.info("Deleting employee with id: {}", id);
        try {
            Employee employee = getEmployeeById(id);
            String url = BASE_URL + "delete/";
            restTemplate.delete(url + id);
            return employee.getName();
        } catch (HttpClientErrorException e) {
            logger.error("Error deleting employee with id: {}", id, e);
            throw new RuntimeException("Error deleting employee", e);
        }
    }

    public String deleteEmployeeAndReturnStatus(String id) {
        logger.info("Deleting employee with id: {}", id);
        try {
            String url = BASE_URL + "delete/";
            var responseEntity = restTemplate.exchange(url + id, DELETE, null, Map.class);
            return (String) Objects.requireNonNull(responseEntity.getBody()).get("status");
        } catch (HttpClientErrorException e) {
            logger.error("Error deleting employee with id: {}", id, e);
            throw new RuntimeException("Error deleting employee", e);
        }
    }
}
