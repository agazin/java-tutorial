package com.axonstech.training.service;

import com.axonstech.training.dto.EmployeeDto;
import com.axonstech.training.dto.request.NewEmployeeRequest;
import com.axonstech.training.entity.Company;
import com.axonstech.training.entity.Employee;
import com.axonstech.training.repository.CompanyRepository;
import com.axonstech.training.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private CompanyRepository companyRepository;

    private EmployeeService employeeService;

    @BeforeEach
    void setUp() {
        employeeService = new EmployeeService(employeeRepository, companyRepository);
    }

    @Test
    void canGetAllEmployees() {
        // arrange  , given
        Boolean onlyActive = null;
        int page = 1;
        int size = 10;
        PageRequest pageRequest = PageRequest.of(0, size);
        Page<Employee> pEmployee = new PageImpl<>(List.of(new Employee()));

        Mockito.when(employeeRepository.findAll(pageRequest)).thenReturn(pEmployee);

        // act      , when
        employeeService.getEmployees(onlyActive, page, size);

        // assert   , then
        verify(employeeRepository).findAll(pageRequest);
    }

    @Test
    void canGetActiveEmployees() {
        // arrange  , given
        Boolean onlyActive = true;
        int page = 1;
        int size = 10;
        PageRequest pageRequest = PageRequest.of(0, size);
        Page<Employee> pEmployee = new PageImpl<>(List.of(new Employee()));

        Mockito.when(employeeRepository.findByActive(onlyActive, pageRequest)).thenReturn(pEmployee);

        // act      , when
        employeeService.getEmployees(onlyActive, page, size);

        // assert   , then
        verify(employeeRepository).findByActive(onlyActive, pageRequest);
    }

    @Test
    void canGetEmployee() {
        // arrange
        Long id = 99L;
        Employee employee = new Employee();
        employee.setCompany(new Company());
        Mockito.when(employeeRepository.findById(id)).thenReturn(Optional.of(employee));

        // act
        EmployeeDto dto = employeeService.getEmployee(id);

        // assert
        assertThat(dto).isInstanceOf(EmployeeDto.class);
    }

    @Test
    void canSaveEmployee() throws Exception {
        // arrange
        NewEmployeeRequest employeeRequest = new NewEmployeeRequest();
        employeeRequest.setUsername("test");
        Mockito.when(employeeRepository.findByUsernameIgnoreCase(employeeRequest.getUsername()))
                .thenReturn(Optional.empty());

        // act
        EmployeeDto dto = employeeService.save(employeeRequest);

        // assert
        assertThat(dto).isInstanceOf(EmployeeDto.class);
    }

    @Test
    void willThrowWhenSaveExistEmployee() {
        // arrange
        NewEmployeeRequest employeeRequest = new NewEmployeeRequest();
        employeeRequest.setUsername("test");
        Employee employee = new Employee();
        Mockito.when(employeeRepository.findByUsernameIgnoreCase(employeeRequest.getUsername()))
                .thenReturn(Optional.of(employee));

        // act
        // assert
        assertThatThrownBy( () ->  employeeService.save(employeeRequest))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("username is already taken");
    }

    @Test
    void canUpdateEmployee() throws Exception {
        //arrange
        EmployeeDto employeeDto = new EmployeeDto();
        Employee emp = new Employee();

        Mockito.when(employeeRepository.findById(employeeDto.getId())).thenReturn(Optional.of(emp));

        //act
        employeeService.update(employeeDto);

        //assert
        ArgumentCaptor<Employee> captor = ArgumentCaptor.forClass(Employee.class);
        verify(employeeRepository).save(captor.capture());
        Employee employee = captor.getValue();
        assertThat(employee).isInstanceOf(Employee.class);
    }

    @Test
    void willThrowWhenUpdateEmployeeNotFound() {
        //arrange
        EmployeeDto employeeDto = new EmployeeDto();

        Mockito.when(employeeRepository.findById(employeeDto.getId())).thenReturn(Optional.empty());

        //act
        //assert
        assertThatThrownBy( () -> employeeService.update(employeeDto))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("Id not found");
    }

    @Test
    void canDelete() throws Exception {
        //arrange
        Long id = 99L;

        //act
        employeeService.delete(id);

        //assert
        verify(employeeRepository).deleteById(id);
    }

    @Test
    void canAddToCompany() throws Exception {
        //arrange
        Long userId = 99L;
        String companyCode = "000";
        Employee emp = new Employee();
        emp.setId(userId);
        Mockito.when(employeeRepository.findById(userId)).thenReturn(Optional.of(emp));
        Mockito.when(companyRepository.findById(companyCode)).thenReturn(Optional.of(new Company()));

        //act
        employeeService.addToCompany(userId, companyCode);

        //assert
        ArgumentCaptor<Employee> captor = ArgumentCaptor.forClass(Employee.class);
        verify(employeeRepository).save(captor.capture());
        Employee employee = captor.getValue();
        assertThat(employee).isInstanceOf(Employee.class);
        assertThat(employee.getId()).isEqualTo(userId);
    }


    @Test
    void willThrowWhenAddToCompanyCuzEmployeeNotFound() {
        //arrange
        Long userId = 99L;
        String companyCode = "000";
        Mockito.when(employeeRepository.findById(userId)).thenReturn(Optional.empty());

        //act

        //assert
        assertThatThrownBy( () -> employeeService.addToCompany(userId, companyCode))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("not found user");
    }

    @Test
    void willThrowWhenAddToCompanyCuzCompanyNotFound() {
        //arrange
        Long userId = 99L;
        String companyCode = "000";
        Mockito.when(employeeRepository.findById(userId)).thenReturn(Optional.of(new Employee()));
        Mockito.when(companyRepository.findById(companyCode)).thenReturn(Optional.empty());

        //act

        //assert
        assertThatThrownBy( () -> employeeService.addToCompany(userId, companyCode))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("not found company");
    }
}