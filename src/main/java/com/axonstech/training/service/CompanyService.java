package com.axonstech.training.service;

import com.axonstech.training.entity.Company;
import com.axonstech.training.exception.BusinessException;
import com.axonstech.training.repository.CompanyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CompanyService {
    private CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public Page<Company> getCompanies(int page, int size) {
        PageRequest pageable = PageRequest.of(page - 1, size);
        return companyRepository.findAll(pageable);
    }

    public Company getCompany(String id) {
        return companyRepository.findById(id).orElseThrow();
    }

    public Company save(Company company) throws BusinessException {
        Optional<Company> oCompany = companyRepository.findByCompanyName(company.getCompanyName());
        if(oCompany.isPresent()){
            throw new BusinessException("This Company is already taken");
        }
        return companyRepository.save(company);
    }

    public Company update(Company company) {
        return companyRepository.save(company);
    }

    public void delete(String id) {
        companyRepository.deleteById(id);
    }

}
