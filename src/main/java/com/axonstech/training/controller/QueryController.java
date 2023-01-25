package com.axonstech.training.controller;

import com.axonstech.training.dto.CompanyDto;
import com.axonstech.training.dto.EmployeeDto;
import com.axonstech.training.entity.Company;
import com.axonstech.training.entity.Employee;
import com.axonstech.training.entity.EmployeeDataInfo;
import com.axonstech.training.repository.CompanyRepository;
import com.axonstech.training.repository.EmployeeRepository;
import com.axonstech.training.util.DatabaseMapper;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/test/api")
@RequiredArgsConstructor
public class QueryController {
    private final EmployeeRepository employeeRepository;
    private final CompanyRepository companyRepository;
    private final JdbcTemplate jdbcTemplate;

    @GetMapping("/jpql/{id}")
    public Employee getEmployeeById(@PathVariable Long id) {
        return employeeRepository.jpqlFindById(id);
    }


    @GetMapping("/native/{id}")
    public EmployeeDto getEmployeeByIdNative(@PathVariable Long id) {
        Employee employee = employeeRepository.nativeFindById(id);
        EmployeeDto empResult = new EmployeeDto();
        BeanUtils.copyProperties(employee, empResult);
        return empResult;
    }

    @GetMapping("/jpql/company/{companyName}")
    public List<Employee> getEmployeeByCompanyName(@PathVariable String companyName) {
        return employeeRepository.jpqlFindByCompanyName(companyName + "%", Sort.by(Sort.Direction.DESC, "id"));
    }

    @GetMapping("/native/company/{companyName}")
    public List<EmployeeDto> getEmployeeByCompanyNameNative(@PathVariable String companyName) {
        List<EmployeeDataInfo> employeeDataInfos = employeeRepository.nativeFindByCompanyName(companyName + "%");
        List<EmployeeDto> dtos = employeeDataInfos.stream().map(employeeDataInfo -> {
            EmployeeDto dto = new EmployeeDto();
            BeanUtils.copyProperties(employeeDataInfo, dto);
            CompanyDto companyDto = new CompanyDto();
            companyDto.setCompanyName(employeeDataInfo.getCompanyName());
            dto.setCompany(companyDto);
            return dto;
        }).toList();
        return dtos;
    }


    @GetMapping("/tuple/company/{companyName}")
    public List<EmployeeDto> getEmployeeByCompanyNameTuple(@PathVariable String companyName) {
        List<Tuple> result = employeeRepository.tupleFindByCompanyName(companyName + "%");
        return DatabaseMapper.getInstance().parseResult(result, EmployeeDto.class);
    }

    @GetMapping("/jpql")
    public Page<Employee> jqplFindAllByActive(@RequestParam Boolean active) {
        return employeeRepository.jqplFindAllByActive(active, Pageable.ofSize(10));
    }

    @GetMapping("/native")
    public Page<EmployeeDto> nativeFindAllByActive(@RequestParam Boolean active
            , @RequestParam(required = false, defaultValue = "1") int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "first_name");
        PageRequest pageable = PageRequest.of(page, size, sort);
        Page<Employee> pEmployee = employeeRepository.nativeFindAllByActive(active, pageable);
        return pEmployee.map(employee -> {
            EmployeeDto dto = new EmployeeDto();
            BeanUtils.copyProperties(employee, dto);
            return dto;
        });
    }

    @GetMapping("/condition")
    public Page<EmployeeDto> queryByCondition(@RequestParam(required = false) Boolean active
                                            , @RequestParam(required = false) String username
                                            , @RequestParam(required = false) String companyName) {
        Specification<Employee> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (username != null && !username.equals("")) {
                predicates.add(criteriaBuilder.equal(root.get("username"), username));
            }
            if (companyName != null && !companyName.equals("")) {
                Join<Employee, Company> groupJoin = root.join("company");
                predicates.add(criteriaBuilder.like(groupJoin.get("companyName"), companyName + "%"));
            }
            if (active != null) {
                predicates.add(criteriaBuilder.equal(root.get("active"), active));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        Page<Employee> employees = employeeRepository.findAll(specification, Pageable.ofSize(10));
        return employees.map(e -> {
            EmployeeDto dto = new EmployeeDto();
            BeanUtils.copyProperties(e, dto);
            return dto;
        });
    }

    @GetMapping("/condition/template")
    public List<EmployeeDto> queryTemplateByCondition(@RequestParam(required = false) Boolean active
            , @RequestParam(required = false) String username
            , @RequestParam(required = false) String companyName){

        StringBuilder sql = new StringBuilder(" select * " +
                "   from employee e" +
                "      left join company c on e.company_code = c.company_code " +
                "   where 1=1 ");
        List<Object> params = new ArrayList<>();
        if (username != null && !username.equals("")) {
            sql.append(" and e.username = ? ");
            params.add(username);
        }
        if (companyName != null && !companyName.equals("")) {
            sql.append(" and c.company_name = ? ");
            params.add(companyName);
        }
        if (active != null) {
            sql.append(" and e.active = ? ");
            params.add(active);
        }
        List<EmployeeDto> resultList = jdbcTemplate.query( sql.toString(), (result ,  rowNum) -> {
            EmployeeDto employeeDto = new EmployeeDto();
            employeeDto.setId(result.getLong("id"));
            employeeDto.setUsername(result.getString("username"));
            employeeDto.setActive(result.getBoolean("active"));
            return employeeDto;
        }, params.toArray());
        return resultList;
    }


}
