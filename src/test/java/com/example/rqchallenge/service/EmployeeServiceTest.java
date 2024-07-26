package com.example.rqchallenge.service;

import com.example.rqchallenge.RqChallengeApplication;
import com.example.rqchallenge.employees.model.EmployeeResponse;
import com.example.rqchallenge.employees.model.Employee;
import com.example.rqchallenge.employees.model.EmployeeCreationResponse;
import com.example.rqchallenge.employees.model.SingleEmployeeResponse;
import com.example.rqchallenge.employees.service.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = RqChallengeApplication.class)
@ActiveProfiles("test")
class EmployeeServiceTest {

    public static final Map<String, Object> EMPLOYEE_INPUT = Map.of("name", "John Doe", "salary", "50000", "age", "30");
    @MockBean
    private RestTemplate restTemplate;

    @Autowired
    private EmployeeService employeeService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
    }

    private EmployeeResponse readJsonForEmployeeResponse(String fileName) throws IOException, URISyntaxException {
        URL resource = getClass().getClassLoader().getResource("mockdata/" + fileName);
        return objectMapper.readValue(
                Files.readString(Paths.get(requireNonNull(resource).toURI())),
                EmployeeResponse.class
        );
    }

    private SingleEmployeeResponse readJsonForSingleEmployeeResponse(String fileName) throws IOException, URISyntaxException {
        URL resource = getClass().getClassLoader().getResource("mockdata/" + fileName);
        return objectMapper.readValue(
                Files.readString(Paths.get(requireNonNull(resource).toURI())),
                SingleEmployeeResponse.class
        );
    }

    private EmployeeCreationResponse readJsonForEmployeeCreationResponse(String fileName) throws IOException, URISyntaxException {
        URL resource = getClass().getClassLoader().getResource("mockdata/" + fileName);
        return objectMapper.readValue(
                Files.readString(Paths.get(requireNonNull(resource).toURI())),
                EmployeeCreationResponse.class
        );
    }

    @Nested
    class GetAllEmployeesTests {
        @Test
        void getAllEmployees_Success() throws IOException, URISyntaxException {
            EmployeeResponse response = readJsonForEmployeeResponse("employees_success.json");
            when(restTemplate.getForObject(anyString(), eq(EmployeeResponse.class))).thenReturn(response);

            List<Employee> employees = employeeService.getAllEmployees();
            assertEquals(24, employees.size());
            assertEquals("Tiger Nixon", employees.get(0).getName());
        }

        @Test
        void getAllEmployees_Failure() {
            when(restTemplate.getForObject(anyString(), eq(Map.class))).thenThrow(new RuntimeException("Service error"));

            Exception exception = assertThrows(RuntimeException.class, () -> {
                employeeService.getAllEmployees();
            });
            assertEquals("Error fetching employees", exception.getMessage());
        }
    }

    @Test
    void getEmployeesByName() throws IOException, URISyntaxException {
        EmployeeResponse response = readJsonForEmployeeResponse("employees_success.json");
        when(restTemplate.getForObject(anyString(), eq(EmployeeResponse.class))).thenReturn(response);

        List<Employee> employees = employeeService.getEmployeesByNameSearch("Tig");
        assertEquals(1, employees.size());
        assertEquals("Tiger Nixon", employees.get(0).getName());
    }

    @Nested
    class GetEmployeeByIdTests {
        @Test
        void getEmployeeById_Success() throws IOException, URISyntaxException {
            SingleEmployeeResponse response = readJsonForSingleEmployeeResponse("single_employee.json");
            when(restTemplate.getForObject(anyString(), eq(SingleEmployeeResponse.class))).thenReturn(response);

            Employee employee = employeeService.getEmployeeById("1");
            assertNotNull(employee);
            assertEquals("Tiger Nixon", employee.getName());
        }

        @Test
        void getEmployeeById_Failure() {
            when(restTemplate.getForObject(anyString(), eq(Map.class))).thenThrow(new RuntimeException("Service error"));

            Exception exception = assertThrows(RuntimeException.class, () -> {
                employeeService.getEmployeeById("1");
            });
            assertEquals("Error fetching employee by id", exception.getMessage());
        }
    }

    @Nested
    class CreateEmployeeTests {
        @Test
        void createEmployee_Success() throws IOException, URISyntaxException {
            EmployeeCreationResponse response = readJsonForEmployeeCreationResponse("create_employee_success.json");
            when(restTemplate.postForObject(anyString(), any(), eq(EmployeeCreationResponse.class))).thenReturn(response);

            Employee employee = employeeService.createEmployee(EMPLOYEE_INPUT);
            assertEquals("John Doe", employee.getName());
        }

        @Test
        void createEmployee_Failure() {
            when(restTemplate.postForObject(anyString(), any(), eq(Map.class))).thenThrow(new RuntimeException("Service error"));

            Exception exception = assertThrows(RuntimeException.class, () -> {
                employeeService.createEmployee(EMPLOYEE_INPUT);
            });
            assertEquals("Error creating employee", exception.getMessage());
        }
    }

    @Nested
    class DeleteEmployeeTests {
        @Test
        void deleteEmployee_Success() throws IOException, URISyntaxException {
            SingleEmployeeResponse response = readJsonForSingleEmployeeResponse("single_employee.json");
            when(restTemplate.getForObject(anyString(), eq(SingleEmployeeResponse.class))).thenReturn(response);
            doNothing().when(restTemplate).delete(anyString());

            String employeeName = employeeService.deleteEmployee("1");
            assertEquals("Tiger Nixon", employeeName);
        }

        @Test
        void deleteEmployee_Failure() throws IOException, URISyntaxException {
            SingleEmployeeResponse response = readJsonForSingleEmployeeResponse("single_employee.json");
            when(restTemplate.getForObject(anyString(), eq(SingleEmployeeResponse.class))).thenReturn(response);
            doThrow(new RuntimeException("Error deleting employee")).when(restTemplate).delete(anyString());

            Exception exception = assertThrows(RuntimeException.class, () -> {
                employeeService.deleteEmployee("1");
            });
            assertEquals("Error deleting employee", exception.getMessage());
        }
    }

    @Nested
    class GetHighestSalaryTests {
        @Test
        void getHighestSalary_Success() throws IOException, URISyntaxException {
            EmployeeResponse response = readJsonForEmployeeResponse("employees_success.json");
            when(restTemplate.getForObject(anyString(), eq(EmployeeResponse.class))).thenReturn(response);

            Integer highestSalary = employeeService.getHighestSalaryOfEmployees();
            assertEquals(725000, highestSalary);
        }

        @Test
        void getHighestSalary_Failure() {
            when(restTemplate.getForObject(anyString(), eq(Map.class))).thenThrow(new RuntimeException("Service error"));

            Exception exception = assertThrows(RuntimeException.class, () -> {
                employeeService.getHighestSalaryOfEmployees();
            });
            assertEquals("Error fetching employees", exception.getMessage());
        }
    }

    @Nested
    class GetTopTenHighestEarningEmployeeNamesTests {
        @Test
        void getTopTenHighestEarningEmployeeNames_Success() throws IOException, URISyntaxException {
            EmployeeResponse response = readJsonForEmployeeResponse("employees_success.json");
            when(restTemplate.getForObject(anyString(), eq(EmployeeResponse.class))).thenReturn(response);

            List<String> topTenNames = employeeService.getTopTenHighestEarningEmployeeNames();
            assertEquals(10, topTenNames.size()); // Adjust based on the number of records in the JSON file
            assertTrue(topTenNames.contains("Cedric Kelly"));
            assertTrue(topTenNames.contains("Paul Byrd"));
        }

        @Test
        void getTopTenHighestEarningEmployeeNames_Failure() {
            when(restTemplate.getForObject(anyString(), eq(Map.class))).thenThrow(new RuntimeException("Service error"));

            Exception exception = assertThrows(RuntimeException.class, () -> employeeService.getTopTenHighestEarningEmployeeNames());
            assertEquals("Error fetching employees", exception.getMessage());
        }
    }
}
