package com.axonstech.training.repository;

import com.axonstech.training.entity.Employee;
import com.axonstech.training.entity.EmployeeDataInfo;
import jakarta.persistence.Tuple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> , JpaSpecificationExecutor<Employee> {
    Page<Employee> findByActive(Boolean active, Pageable pageable);

    Optional<Employee> findByUsernameIgnoreCase(@NonNull String username);

    @Query("select e from Employee e where e.id = ?1")
    Employee jpqlFindById(Long id);

    @Query(value = "select * from employee where id = :id", nativeQuery = true)
    Employee nativeFindById(Long id);

    @Query("select e from Employee e where e.company.companyName like :companyName ")
    List<Employee> jpqlFindByCompanyName(String companyName, Sort sort);

    @Query(value = "select e.id id, e.username username, e.active active, c.company_name companyName from Employee e " +
            "   left join company c on e.company_code = c.company_code" +
            " where c.company_name like :companyName " +
            " order by e.id desc "
            , nativeQuery = true)
    List<EmployeeDataInfo> nativeFindByCompanyName(String companyName);

    @Query(value = "select e.id id, e.username username, e.active active, c.company_name companyName from Employee e " +
            "   left join company c on e.company_code = c.company_code" +
            " where c.company_name like :companyName " +
            " order by e.id desc "
            , nativeQuery = true)
    List<Tuple> tupleFindByCompanyName(String companyName);

    @Query("select e from Employee e where e.active = ?1")
    Page<Employee> jqplFindAllByActive(Boolean active, Pageable pageable);

    @Query(value = "select * from Employee e where e.active = :active"
            , countQuery = "select count(*) from Employee e where e.active = :active"
            , nativeQuery = true)
    Page<Employee> nativeFindAllByActive(Boolean active, Pageable pageable);


}