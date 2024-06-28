package com.coherentsolutions.pot.insurance.repository;

import com.coherentsolutions.pot.insurance.entity.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EmployeeRepository extends JpaRepository<EmployeeEntity, UUID>{
}