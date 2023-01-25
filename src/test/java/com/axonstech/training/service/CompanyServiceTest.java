package com.axonstech.training.service;

import com.axonstech.training.dto.CompanyDto;
import com.axonstech.training.dto.request.NewEmployeeRequest;
import com.axonstech.training.entity.Company;
import com.axonstech.training.entity.Employee;
import com.axonstech.training.repository.CompanyRepository;
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
class CompanyServiceTest {
    @Mock
    private CompanyRepository companyRepository;

    private CompanyService companyService;

    @BeforeEach
    void setUp() {
        companyService = new CompanyService(companyRepository);
    }

    @Test
    void canGetAllCompany() {
        // arrange  , given
        Boolean onlyActive = null;
        int page = 1;
        int size = 10;
        PageRequest pageRequest = PageRequest.of(0, size);
        Page<Company> companyPage = new PageImpl<>(List.of(new Company()));

        Mockito.when(companyRepository.findAll(pageRequest)).thenReturn(companyPage);

        // act      , when
        companyService.getCompanies(page, size);

        // assert   , then
        verify(companyRepository).findAll(pageRequest);
    }

    @Test
    void canGetEmployee() {
        // arrange
        String companyCode = "001";
        Mockito.when(companyRepository.findById(companyCode)).thenReturn(Optional.of(new Company()));

        // act
        Company company = companyService.getCompany(companyCode);

        // assert
        assertThat(company).isInstanceOf(Company.class);
    }

    @Test
    void canSaveEmployee() throws Exception {
        Company company = new Company();
        company.setCompanyCode("001");
        company.setCompanyName("TEST");
        // arrange
        Mockito.when(companyRepository.findByCompanyName(company.getCompanyName()))
                .thenReturn(Optional.empty());
        // act
         companyService.save(company);

        // assert
        ArgumentCaptor<Company> captor = ArgumentCaptor.forClass(Company.class);
        verify(companyRepository).save(captor.capture());
    }

    @Test
    void willThrowWhenSaveExistEmployee() {
        Company company = new Company();
        company.setCompanyCode("001");
        company.setCompanyName("TEST");
        // arrange
        Mockito.when(companyRepository.findByCompanyName(company.getCompanyName()))
                .thenReturn(Optional.of(company));
        // act
        // assert
        assertThatThrownBy( () ->  companyService.save(company))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("This Company is already taken");
    }

    @Test
    void canUpdateEmployee() throws Exception {
        //arrange
        Company employeeDto = new Company();

        //act
        companyService.update(employeeDto);

        //assert
        ArgumentCaptor<Company> captor = ArgumentCaptor.forClass(Company.class);
        verify(companyRepository).save(captor.capture());
    }


    @Test
    void canDelete() {
        //arrange
        String companyCode = "001";

        //act
        companyService.delete(companyCode);

        //assert
        verify(companyRepository).deleteById(companyCode);
    }

}