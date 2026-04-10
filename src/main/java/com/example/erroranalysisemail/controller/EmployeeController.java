package com.example.erroranalysisemail.controller;

import com.example.erroranalysisemail.exception.EmployeeNotFoundException;
import com.example.erroranalysisemail.exception.InvalidActionException;
import com.example.erroranalysisemail.model.Employee;
import com.example.erroranalysisemail.service.EmployeeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/employee")
@CrossOrigin(origins = "*")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    /**
     * Single API endpoint for all employee operations
     * {
     *   "action": "GET_ALL" | "GET_BY_ID" | "CREATE" | "UPDATE" | "DELETE" | "FILTER" | "GET_TEAMS",
     *   "id": 1,
     *   "employee": { ... },
     *   "filters": { "team": "...", "designation": "...", "minSalary": 50000, "maxSalary": 100000, "name": "..." }
     * }
     */
    @PostMapping
    public ResponseEntity<?> employeeApi(@RequestBody Map<String, Object> request) {
        String action = (String) request.get("action");

        if (action == null) {
            throw new IllegalArgumentException("Action is required");
        }

        switch (action.toUpperCase()) {
            case "GET_ALL":
                return ResponseEntity.ok(employeeService.getAll());

            case "GET_BY_ID":
                Long getId = Long.valueOf(request.get("id").toString());
                Employee emp = employeeService.getById(getId);
                if (emp == null) {
                    throw new EmployeeNotFoundException(getId);
                }
                return ResponseEntity.ok(emp);

            case "CREATE":
                Map<String, Object> createData = (Map<String, Object>) request.get("employee");
                Employee newEmp = mapToEmployee(createData);
                return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.create(newEmp));

            case "UPDATE":
                Long updateId = Long.valueOf(request.get("id").toString());
                Map<String, Object> updateData = (Map<String, Object>) request.get("employee");
                Employee updEmp = mapToEmployee(updateData);
                Employee result = employeeService.update(updateId, updEmp);
                if (result == null) {
                    throw new EmployeeNotFoundException(updateId);
                }
                return ResponseEntity.ok(result);

            case "DELETE":
                Long deleteId = Long.valueOf(request.get("id").toString());
                boolean deleted = employeeService.delete(deleteId);
                if (!deleted) {
                    throw new EmployeeNotFoundException(deleteId);
                }
                return ResponseEntity.noContent().build();

            case "FILTER":
                Map<String, Object> filters = (Map<String, Object>) request.getOrDefault("filters", new HashMap<>());
                String team = (String) filters.get("team");
                String designation = (String) filters.get("designation");
                Double minSalary = filters.get("minSalary") != null ? Double.valueOf(filters.get("minSalary").toString()) : null;
                Double maxSalary = filters.get("maxSalary") != null ? Double.valueOf(filters.get("maxSalary").toString()) : null;
                String name = (String) filters.get("name");
                return ResponseEntity.ok(employeeService.filter(team, designation, minSalary, maxSalary, name));

            case "GET_TEAMS":
                return ResponseEntity.ok(employeeService.getTeams());

            default:
                throw new InvalidActionException(action);
        }
    }

    private Employee mapToEmployee(Map<String, Object> data) {
        Employee emp = new Employee();
        if (data.get("id") != null) {
            emp.setId(Long.valueOf(data.get("id").toString()));
        }
        emp.setName((String) data.get("name"));
        emp.setEmail((String) data.get("email"));
        emp.setDesignation((String) data.get("designation"));
        if (data.get("salary") != null) {
            emp.setSalary(Double.valueOf(data.get("salary").toString()));
        }
        emp.setTeam((String) data.get("team"));
        return emp;
    }
}
