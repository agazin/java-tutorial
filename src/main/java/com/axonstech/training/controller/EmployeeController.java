package com.axonstech.training.controller;

import com.axonstech.training.dto.EmployeeDto;
import com.axonstech.training.dto.request.NewEmployeeRequest;
import com.axonstech.training.entity.Employee;
import com.axonstech.training.service.EmployeeService;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping // localhost:18080/api/employees
    public Page<EmployeeDto> getAllEmp(@RequestParam(name = "onlyActive", required = false) Boolean onlyActives,
                                    @RequestParam(required = false, defaultValue = "1") int page,
                                    @RequestParam(required = false, defaultValue = "10") int size) {
        return employeeService.getEmployees(onlyActives, page, size);
    }

    @GetMapping("/{id}") //localhost:18080/api/employees/111
    public EmployeeDto getEmp(@PathVariable Long id){
        return employeeService.getEmployee(id);
    }


    @PostMapping
    public EmployeeDto saveEmp(@RequestBody NewEmployeeRequest employeeDto) throws Exception {
        return employeeService.save(employeeDto);
    }


    @PutMapping
    public EmployeeDto updateEmp(@RequestBody EmployeeDto employee) throws Exception {
        return employeeService.update(employee);
    }


    @DeleteMapping("/{id}")
    public void deleteEmp(@PathVariable Long id) {
        employeeService.delete(id);
    }

    @PostMapping("/{id}/company/{companyCode}")
    public Employee addToCompany(@PathVariable Long id ,@PathVariable String companyCode) throws Exception {
        return employeeService.addToCompany(id, companyCode);
    }
}
