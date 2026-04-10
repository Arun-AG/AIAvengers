package com.example.erroranalysisemail.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EmployeeTest {

    @Test
    void testDefaultConstructor() {
        Employee employee = new Employee();
        assertNull(employee.getId());
        assertNull(employee.getName());
        assertNull(employee.getEmail());
        assertNull(employee.getDesignation());
        assertNull(employee.getSalary());
        assertNull(employee.getTeam());
    }

    @Test
    void testParameterizedConstructor() {
        Employee employee = new Employee(1L, "John Doe", "john@company.com", "Developer", 75000.0, "Engineering");
        
        assertEquals(1L, employee.getId());
        assertEquals("John Doe", employee.getName());
        assertEquals("john@company.com", employee.getEmail());
        assertEquals("Developer", employee.getDesignation());
        assertEquals(75000.0, employee.getSalary());
        assertEquals("Engineering", employee.getTeam());
    }

    @Test
    void testSettersAndGetters() {
        Employee employee = new Employee();
        
        employee.setId(2L);
        employee.setName("Jane Smith");
        employee.setEmail("jane@company.com");
        employee.setDesignation("Manager");
        employee.setSalary(85000.0);
        employee.setTeam("Sales");
        
        assertEquals(2L, employee.getId());
        assertEquals("Jane Smith", employee.getName());
        assertEquals("jane@company.com", employee.getEmail());
        assertEquals("Manager", employee.getDesignation());
        assertEquals(85000.0, employee.getSalary());
        assertEquals("Sales", employee.getTeam());
    }
}
