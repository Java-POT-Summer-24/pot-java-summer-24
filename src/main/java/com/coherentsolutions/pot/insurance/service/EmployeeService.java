package com.coherentsolutions.pot.insurance.service;

import com.coherentsolutions.pot.insurance.constants.EmployeeStatus;
import com.coherentsolutions.pot.insurance.dto.EmployeeDTO;
import com.coherentsolutions.pot.insurance.entity.EmployeeEntity;
import com.coherentsolutions.pot.insurance.exception.NotFoundException;
import com.coherentsolutions.pot.insurance.mapper.EmployeeMapper;
import com.coherentsolutions.pot.insurance.repository.EmployeeRepository;
import com.coherentsolutions.pot.insurance.specifications.EmployeeFilterCriteria;
import com.coherentsolutions.pot.insurance.specifications.EmployeeSpecifications;
import com.coherentsolutions.pot.insurance.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployeeService {

  private final EmployeeRepository employeeRepository;

  private final WebClient webClient = WebClient.create();

  private static final String NOTIFICATION_SERVICE_URL = "http://localhost:8081/v1/notification/send";

  public Page<EmployeeDTO> getFilteredSortedEmployees(EmployeeFilterCriteria employeeFilterCriteria,
      Pageable pageable) {
    Sort defaultSort = Sort.by("dateOfBirth").descending();

    if (!pageable.getSort().isSorted()) {
      pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), defaultSort);
    }

    Specification<EmployeeEntity> spec = buildSpecification(employeeFilterCriteria);

    return employeeRepository.findAll(spec, pageable)
        .map(EmployeeMapper.INSTANCE::employeeToEmployeeDTO);
  }

  public EmployeeDTO addEmployee(EmployeeDTO employeeDTO){
    EmployeeEntity employee = EmployeeMapper.INSTANCE.employeeDTOToEmployee(employeeDTO);
    EmployeeEntity createdEmployee = employeeRepository.save(employee);

    return EmployeeMapper.INSTANCE.employeeToEmployeeDTO(createdEmployee);
  }

  public EmployeeDTO getEmployee(UUID employeeId) {
    return employeeRepository.findById(employeeId)
        .map(EmployeeMapper.INSTANCE::employeeToEmployeeDTO)
        .orElseThrow(() -> new NotFoundException("Employee not found with id: " + employeeId));
  }

  public List<EmployeeDTO> getAllEmployees() {
    return employeeRepository.findAll().stream()
        .map(EmployeeMapper.INSTANCE::employeeToEmployeeDTO)
        .toList();
  }

  public EmployeeDTO updateEmployee(UUID employeeId, EmployeeDTO updatedEmployeeDTO) {
    return employeeRepository.findById(employeeId)
        .map(employee -> {
          if (employee.getStatus() == EmployeeStatus.INACTIVE) {
            throw new NotFoundException(
                "Cannot update. Employee with id: " + employeeId + " is inactive");
          }
          EmployeeMapper.INSTANCE.updateEmployeeFromDTO(updatedEmployeeDTO, employee);
          return EmployeeMapper.INSTANCE.employeeToEmployeeDTO(employeeRepository.save(employee));
        })
        .orElseThrow(() -> new NotFoundException("Employee not found with id: " + employeeId));
  }

  public EmployeeDTO deactivateEmployee(UUID employeeId) {
    return employeeRepository.findById(employeeId)
        .map(employee -> {
          if (employee.getStatus() == EmployeeStatus.INACTIVE) {
            throw new NotFoundException(
                "Cannot deactivate. Employee with id: " + employeeId + " is already inactive");
          }
          employee.setStatus(EmployeeStatus.INACTIVE);
          employee = employeeRepository.save(employee);

          // Send notification
          sendDeactivationNotification(employee);

          return EmployeeMapper.INSTANCE.employeeToEmployeeDTO(employee);
        })
        .orElseThrow(() -> new NotFoundException("Employee with ID " + employeeId + " was not found"));
  }

  private void sendDeactivationNotification(EmployeeEntity employee) {
    Map<String, String> notificationPayload = new HashMap<>();
    notificationPayload.put("subject", "Account Deactivated");
    notificationPayload.put("message", "Dear " + employee.getFirstName() + ",\n\nYour account has been deactivated.\n\nBest regards,\nYour Company");

    webClient.post()
        .uri(NOTIFICATION_SERVICE_URL + "/" + employee.getEmail())
        .body(Mono.just(notificationPayload), Map.class)
        .retrieve()
        .bodyToMono(String.class)
        .block();
  }

  private Specification<EmployeeEntity> buildSpecification(
      EmployeeFilterCriteria employeeFilterCriteria) {
    Specification<EmployeeEntity> spec = Specification.where(null);

    if (ValidationUtil.isNotEmpty(employeeFilterCriteria.getFirstName())) {
      spec = spec.and(EmployeeSpecifications.byFirstName(employeeFilterCriteria.getFirstName()));
    }
    if (ValidationUtil.isNotEmpty(employeeFilterCriteria.getLastName())) {
      spec = spec.and(EmployeeSpecifications.byLastName(employeeFilterCriteria.getLastName()));
    }
    if (ValidationUtil.isNotEmpty(employeeFilterCriteria.getUserName())) {
      spec = spec.and(EmployeeSpecifications.byUserName(employeeFilterCriteria.getUserName()));
    }
    if (ValidationUtil.isNotEmpty(employeeFilterCriteria.getDateOfBirth())) {
      spec = spec.and(
          EmployeeSpecifications.byDateOfBirth(employeeFilterCriteria.getDateOfBirth()));
    }
    if (ValidationUtil.isNotEmpty(employeeFilterCriteria.getSsn())) {
      spec = spec.and(EmployeeSpecifications.bySsn(employeeFilterCriteria.getSsn()));
    }
    if (ValidationUtil.isNotEmpty(employeeFilterCriteria.getStatus())) {
      spec = spec.and(EmployeeSpecifications.byStatus(employeeFilterCriteria.getStatus()));
    }

    return spec;
  }
}
