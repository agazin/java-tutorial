package com.axonstech.training.dto;

import com.axonstech.training.entity.Company;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * A DTO for the {@link com.axonstech.training.entity.Employee} entity
 */
@Data
public class EmployeeDto implements Serializable {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private Boolean active;
    private CompanyDto company;
    private Integer version;
    private String companyName;
}