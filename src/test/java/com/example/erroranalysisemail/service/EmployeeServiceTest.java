package com.example.erroranalysisemail.service;

import com.example.erroranalysisemail.model.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class EmployeeServiceTest {

    private EmployeeService employeeService;

    @BeforeEach
    void setUp() {
        employeeService = new EmployeeService();
        employeeService.init(); // Load sample data
    }

    @Test
    void testInit() {
        List<Employee> employees = employeeService.getAll();
        assertEquals(25, employees.size());
    }

    @Test
    void testGetAll() {
        List<Employee> employees = employeeService.getAll();
        assertNotNull(employees);
        assertEquals(25, employees.size());
    }

    @Test
    void testGetById() {
        Employee employee = employeeService.getById(1L);
        assertNotNull(employee);
        assertEquals(1L, employee.getId());
        assertEquals("Alice Johnson", employee.getName());
    }

    @Test
    void testGetByIdNotFound() {
        Employee employee = employeeService.getById(999L);
        assertNull(employee);
    }

    @Test
    void testCreate() {
        Employee newEmployee = new Employee();
        newEmployee.setName("Test User");
        newEmployee.setEmail("test@company.com");
        newEmployee.setDesignation("Tester");
        newEmployee.setSalary(50000.0);
        newEmployee.setTeam("Engineering");

        Employee created = employeeService.create(newEmployee);
        assertNotNull(created);
        assertEquals(26L, created.getId()); // Next ID after 25 pre-loaded employees
        assertEquals("Test User", created.getName());
    }

    @Test
    void testUpdate() {
        Employee employee = employeeService.getById(1L);
        employee.setSalary(100000.0);
        
        Employee updated = employeeService.update(1L, employee);
        assertNotNull(updated);
        assertEquals(100000.0, updated.getSalary());
    }

    @Test
    void testUpdateNotFound() {
        Employee employee = new Employee();
        employee.setName("Non-existent");
        
        Employee updated = employeeService.update(999L, employee);
        assertNull(updated);
    }

    @Test
    void testDelete() {
        boolean deleted = employeeService.delete(1L);
        assertTrue(deleted);
        
        Employee employee = employeeService.getById(1L);
        assertNull(employee);
    }

    @Test
    void testDeleteNotFound() {
        boolean deleted = employeeService.delete(999L);
        assertFalse(deleted);
    }

    @Test
    void testFilterByTeam() {
        List<Employee> engineers = employeeService.filter("Engineering", null, null, null, null);
        assertEquals(5, engineers.size());
        engineers.forEach(emp -> assertEquals("Engineering", emp.getTeam()));
    }

    @Test
    void testFilterByDesignation() {
        List<Employee> managers = employeeService.filter(null, "Manager", null, null, null);
        assertTrue(managers.size() > 0);
        managers.forEach(emp -> assertTrue(emp.getDesignation().toLowerCase().contains("manager")));
    }

    @Test
    void testFilterBySalaryRange() {
        List<Employee> midRange = employeeService.filter(null, null, 60000.0, 80000.0, null);
        assertTrue(midRange.size() > 0);
        midRange.forEach(emp -> {
            assertTrue(emp.getSalary() >= 60000.0);
            assertTrue(emp.getSalary() <= 80000.0);
        });
    }

    @Test
    void testFilterByName() {
        List<Employee> alices = employeeService.filter(null, null, null, null, "Alice");
        assertEquals(1, alices.size());
        assertEquals("Alice Johnson", alices.get(0).getName());
    }

    @Test
    void testFilterMultipleCriteria() {
        List<Employee> results = employeeService.filter("Engineering", "Engineer", 70000.0, 90000.0, null);
        assertTrue(results.size() > 0);
        results.forEach(emp -> {
            assertEquals("Engineering", emp.getTeam());
            assertTrue(emp.getDesignation().toLowerCase().contains("engineer"));
            assertTrue(emp.getSalary() >= 70000.0);
            assertTrue(emp.getSalary() <= 90000.0);
        });
    }

    @Test
    void testGetTeams() {
        List<String> teams = employeeService.getTeams();
        assertEquals(5, teams.size());
        assertTrue(teams.contains("Engineering"));
        assertTrue(teams.contains("Sales"));
        assertTrue(teams.contains("Marketing"));
        assertTrue(teams.contains("Human Resources"));
        assertTrue(teams.contains("Finance"));
    }
}
