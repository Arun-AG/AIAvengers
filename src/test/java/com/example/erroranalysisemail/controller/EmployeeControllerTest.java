package com.example.erroranalysisemail.controller;

import com.example.erroranalysisemail.exception.EmployeeNotFoundException;
import com.example.erroranalysisemail.exception.InvalidActionException;
import com.example.erroranalysisemail.model.Employee;
import com.example.erroranalysisemail.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmployeeControllerTest {

    private EmployeeController employeeController;
    private EmployeeService employeeService;

    @BeforeEach
    void setUp() {
        employeeService = mock(EmployeeService.class);
        employeeController = new EmployeeController(employeeService);
    }

    @Test
    void testGetAll() {
        List<Employee> employees = Arrays.asList(new Employee(1L, "Test", "test@company.com", "Dev", 50000.0, "Engineering"));
        when(employeeService.getAll()).thenReturn(employees);

        Map<String, Object> request = Map.of("action", "GET_ALL");
        ResponseEntity<?> response = employeeController.employeeApi(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(employees, response.getBody());
    }

    @Test
    void testGetById() {
        Employee employee = new Employee(1L, "Test", "test@company.com", "Dev", 50000.0, "Engineering");
        when(employeeService.getById(1L)).thenReturn(employee);

        Map<String, Object> request = Map.of("action", "GET_BY_ID", "id", 1L);
        ResponseEntity<?> response = employeeController.employeeApi(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(employee, response.getBody());
    }

    @Test
    void testGetByIdNotFound() {
        when(employeeService.getById(999L)).thenReturn(null);

        Map<String, Object> request = Map.of("action", "GET_BY_ID", "id", 999L);
        
        assertThrows(EmployeeNotFoundException.class, () -> {
            employeeController.employeeApi(request);
        });
    }

    @Test
    void testCreate() {
        Employee newEmployee = new Employee(null, "New", "new@company.com", "Dev", 60000.0, "Engineering");
        Employee created = new Employee(1L, "New", "new@company.com", "Dev", 60000.0, "Engineering");
        when(employeeService.create(any(Employee.class))).thenReturn(created);

        Map<String, Object> employeeData = Map.of(
            "name", "New",
            "email", "new@company.com",
            "designation", "Dev",
            "salary", 60000.0,
            "team", "Engineering"
        );

        Map<String, Object> request = Map.of("action", "CREATE", "employee", employeeData);
        ResponseEntity<?> response = employeeController.employeeApi(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(created, response.getBody());
    }

    @Test
    void testUpdate() {
        Employee updated = new Employee(1L, "Updated", "updated@company.com", "Senior Dev", 80000.0, "Engineering");
        when(employeeService.update(eq(1L), any(Employee.class))).thenReturn(updated);

        Map<String, Object> employeeData = Map.of(
            "name", "Updated",
            "email", "updated@company.com",
            "designation", "Senior Dev",
            "salary", 80000.0,
            "team", "Engineering"
        );

        Map<String, Object> request = Map.of("action", "UPDATE", "id", 1L, "employee", employeeData);
        ResponseEntity<?> response = employeeController.employeeApi(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updated, response.getBody());
    }

    @Test
    void testUpdateNotFound() {
        when(employeeService.update(eq(999L), any(Employee.class))).thenReturn(null);

        Map<String, Object> employeeData = Map.of("name", "Test");
        Map<String, Object> request = Map.of("action", "UPDATE", "id", 999L, "employee", employeeData);

        assertThrows(EmployeeNotFoundException.class, () -> {
            employeeController.employeeApi(request);
        });
    }

    @Test
    void testDelete() {
        when(employeeService.delete(1L)).thenReturn(true);

        Map<String, Object> request = Map.of("action", "DELETE", "id", 1L);
        ResponseEntity<?> response = employeeController.employeeApi(request);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testDeleteNotFound() {
        when(employeeService.delete(999L)).thenReturn(false);

        Map<String, Object> request = Map.of("action", "DELETE", "id", 999L);

        assertThrows(EmployeeNotFoundException.class, () -> {
            employeeController.employeeApi(request);
        });
    }

    @Test
    void testFilter() {
        List<Employee> filtered = Arrays.asList(new Employee(1L, "Test", "test@company.com", "Dev", 50000.0, "Engineering"));
        when(employeeService.filter("Engineering", null, null, null, null)).thenReturn(filtered);

        Map<String, Object> filters = Map.of("team", "Engineering");
        Map<String, Object> request = Map.of("action", "FILTER", "filters", filters);

        ResponseEntity<?> response = employeeController.employeeApi(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(filtered, response.getBody());
    }

    @Test
    void testGetTeams() {
        List<String> teams = Arrays.asList("Engineering", "Sales", "Marketing", "Human Resources", "Finance");
        when(employeeService.getTeams()).thenReturn(teams);

        Map<String, Object> request = Map.of("action", "GET_TEAMS");
        ResponseEntity<?> response = employeeController.employeeApi(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(teams, response.getBody());
    }

    @Test
    void testInvalidAction() {
        Map<String, Object> request = Map.of("action", "INVALID_ACTION");

        assertThrows(InvalidActionException.class, () -> {
            employeeController.employeeApi(request);
        });
    }

    @Test
    void testMissingAction() {
        Map<String, Object> request = Map.of();

        assertThrows(IllegalArgumentException.class, () -> {
            employeeController.employeeApi(request);
        });
    }
}
