package com.coherentsolutions.pot.insurance.service;

import com.coherentsolutions.pot.insurance.constants.EmployeeStatus;
import com.coherentsolutions.pot.insurance.dto.EmployeeDTO;
import com.coherentsolutions.pot.insurance.entity.EmployeeEntity;
import com.coherentsolutions.pot.insurance.exception.NotFoundException;
import com.coherentsolutions.pot.insurance.mapper.EmployeeMapper;
import com.coherentsolutions.pot.insurance.repository.EmployeeRepository;
import com.coherentsolutions.pot.insurance.specifications.EmployeeFilterCriteria;
import com.coherentsolutions.pot.insurance.specifications.EmployeeSpecifications;
import com.coherentsolutions.pot.insurance.util.NotificationClient;
import com.coherentsolutions.pot.insurance.util.ValidationUtil;
import java.util.stream.Collectors;
import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmployeeService {

  private final EmployeeRepository employeeRepository;
  private final NotificationClient notificationClient;

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

  public EmployeeDTO addEmployee(EmployeeDTO employeeDTO) {
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
        .collect(Collectors.toList());
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
                "Cannot deactivate. Employee with ID: " + employeeId + " is already inactive");
          }
          employee.setStatus(EmployeeStatus.INACTIVE);
          employee = employeeRepository.save(employee);

          ResourceBundle messages = ResourceBundle.getBundle("notif_msg", Locale.getDefault());

          String messageTemplate = messages.getString("employee.deactivation.message");
          String message = MessageFormat.format(messageTemplate, employee.getFirstName());

          // Send notification
          notificationClient.sendDeactivationNotification(employee.getEmail(), "Account Deactivated", message);

          return EmployeeMapper.INSTANCE.employeeToEmployeeDTO(employee);
        })
        .orElseThrow(
            () -> new NotFoundException("Employee with ID " + employeeId + " was not found"));
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
    if (ValidationUtil.isNotEmpty(employeeFilterCriteria.getCompanyName())) {
      spec = spec.and(EmployeeSpecifications.byCompany(employeeFilterCriteria.getCompanyName()));
    }

    return spec;
  }
}