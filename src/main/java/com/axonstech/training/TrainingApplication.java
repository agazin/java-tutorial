package com.axonstech.training;

import com.axonstech.training.entity.Company;
import com.axonstech.training.entity.Employee;
import com.axonstech.training.repository.CompanyRepository;
import com.axonstech.training.repository.EmployeeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TrainingApplication {

    public static void main(String[] args) {
        SpringApplication.run(TrainingApplication.class, args);
    }

    @Bean
    CommandLineRunner init(EmployeeRepository employeeRepository, CompanyRepository companyRepository) {
        return args -> {
            Company company = new Company();
            company.setCompanyCode("001");
            company.setCompanyName("TEST");
            companyRepository.save(company);
            for (int i = 1; i < 21; i++) {
                Employee employee = new Employee();
                employee.setUsername("test" + i);
                employee.setPassword("pass" + i);
                employee.setFirstName("name" + i);
                employee.setActive(i % 2 == 0);
                employee.setCompany(company);
                employeeRepository.save(employee);
            }
        };
    }

}
