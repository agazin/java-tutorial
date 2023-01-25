package com.axonstech.training.service;

import com.axonstech.training.controller.EmployeeController;
import com.axonstech.training.dto.CompanyDto;
import com.axonstech.training.dto.EmployeeDto;
import com.axonstech.training.dto.request.NewEmployeeRequest;
import com.axonstech.training.entity.Employee;
import com.axonstech.training.repository.EmployeeRepository;
import com.axonstech.training.entity.Company;
import com.axonstech.training.repository.CompanyRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class EmployeeService {

    private EmployeeRepository employeeRepository;
    private CompanyRepository companyRepository;


    public EmployeeService(EmployeeRepository employeeRepository, CompanyRepository companyRepository) {
        this.employeeRepository = employeeRepository;
        this.companyRepository = companyRepository;
    }

    public EmployeeDto getEmployee(Long id) {
        Employee employee = employeeRepository.findById(id).get();
        EmployeeDto employeeDto = new EmployeeDto();
        BeanUtils.copyProperties(employee, employeeDto);
        Company company = employee.getCompany();
        if(company != null) {
            CompanyDto companyDto = new CompanyDto();
            BeanUtils.copyProperties(company, companyDto);
            employeeDto.setCompany(companyDto);
        }
        return employeeDto;
    }

    public Page<EmployeeDto> getEmployees(Boolean onlyActives, int page, int size) {
        PageRequest pageable = PageRequest.of(page - 1, size);
        if(onlyActives != null ){
            Page<Employee> pEmployee = employeeRepository.findByActive(onlyActives.booleanValue(), pageable);
            return pEmployee.map(e -> {
                EmployeeDto dto = new EmployeeDto();
                BeanUtils.copyProperties(e, dto);
                return dto;
            });
        }
        return employeeRepository.findAll(pageable).map(e -> {
            EmployeeDto dto = new EmployeeDto();
            BeanUtils.copyProperties(e, dto);
            return dto;
        });
    }

    public EmployeeDto save(NewEmployeeRequest employeeRequest) throws Exception {
        Optional<Employee> oEmployee = employeeRepository.findByUsernameIgnoreCase(employeeRequest.getUsername());
        if(oEmployee.isPresent()){
            throw new Exception("username is already taken");
        }
        Employee employee = oEmployee.orElseGet(Employee::new);
        BeanUtils.copyProperties(employeeRequest, employee,"password");
        employeeRequest.setId(employee.getId());
        employeeRepository.save(employee);

        EmployeeDto dto =  new EmployeeDto();
        BeanUtils.copyProperties(employee, dto);
        return dto;
    }

    public EmployeeDto update(EmployeeDto employeeDto) throws Exception {
        Optional<Employee> oEmployee = employeeRepository.findById(employeeDto.getId());
        if(oEmployee.isEmpty()){
            throw new Exception("Id not found");
        }
        Employee employee = oEmployee.orElseGet(Employee::new);
        BeanUtils.copyProperties(employeeDto, employee,"password");
        employeeDto.setId(employee.getId());
        employeeDto.setVersion(employee.getVersion());
        employeeRepository.save(employee);
        return employeeDto;
    }

    public void delete(Long id) {
        employeeRepository.deleteById(id);
    }

    public Employee addToCompany(Long userId, String companyCode) throws Exception {
        Optional<Employee> oEmployee = employeeRepository.findById(userId);
        if(oEmployee.isEmpty()){
            throw new Exception("user not found");
        }
        Optional<Company> oCompany = companyRepository.findById(companyCode);
        if(oCompany.isEmpty()){
            throw new Exception("not found company");
        }

        Employee employee = oEmployee.get();
        employee.setCompany(oCompany.get());

        return employeeRepository.save(employee);
    }
}
