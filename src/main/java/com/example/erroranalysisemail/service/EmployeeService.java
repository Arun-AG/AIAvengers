package com.example.erroranalysisemail.service;

import com.example.erroranalysisemail.model.Employee;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class EmployeeService {
    
    private final Map<Long, Employee> employeeStore = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    
    private static final String[] TEAMS = {"Engineering", "Sales", "Marketing", "Human Resources", "Finance"};
    
    @PostConstruct
    public void init() {
        String[][] data = {
           
        };
        
        for (String[] row : data) {
            Employee emp = new Employee(
                idGenerator.getAndIncrement(),
                row[0], row[1], row[2], Double.parseDouble(row[3]), row[4]
            );
            employeeStore.put(emp.getId(), emp);
        }
    }
    
    public List<Employee> getAll() {
        return new ArrayList<>(employeeStore.values());
    }
    
    public Employee getById(Long id) {
        return employeeStore.get(id);
    }
    
    public Employee create(Employee emp) {
        Long id = idGenerator.getAndIncrement();
        emp.setId(id);
        employeeStore.put(id, emp);
        return emp;
    }
    
    public Employee update(Long id, Employee emp) {
        if (!employeeStore.containsKey(id)) return null;
        emp.setId(id);
        employeeStore.put(id, emp);
        return emp;
    }
    
    public boolean delete(Long id) {
        return employeeStore.remove(id) != null;
    }
    
    public List<Employee> filter(String team, String designation, Double minSalary, Double maxSalary, String name) {
        return employeeStore.values().stream()
            .filter(e -> team == null || e.getTeam().equalsIgnoreCase(team))
            .filter(e -> designation == null || e.getDesignation().toLowerCase().contains(designation.toLowerCase()))
            .filter(e -> minSalary == null || e.getSalary() >= minSalary)
            .filter(e -> maxSalary == null || e.getSalary() <= maxSalary)
            .filter(e -> name == null || e.getName().toLowerCase().contains(name.toLowerCase()))
            .collect(Collectors.toList());
    }
    
    public List<String> getTeams() {
        return Arrays.asList(TEAMS);
    }
}
