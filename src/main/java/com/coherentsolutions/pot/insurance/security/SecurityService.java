package com.coherentsolutions.pot.insurance.security;

import com.coherentsolutions.pot.insurance.dto.ClaimDTO;
import com.coherentsolutions.pot.insurance.dto.CompanyDTO;
import com.coherentsolutions.pot.insurance.dto.EmployeeDTO;
import com.coherentsolutions.pot.insurance.entity.ClaimEntity;
import com.coherentsolutions.pot.insurance.entity.CompanyEntity;
import com.coherentsolutions.pot.insurance.entity.EmployeeEntity;
import com.coherentsolutions.pot.insurance.exception.UnauthorizedAccessException;
import com.coherentsolutions.pot.insurance.mapper.ClaimMapper;
import com.coherentsolutions.pot.insurance.mapper.CompanyMapper;
import com.coherentsolutions.pot.insurance.mapper.EmployeeMapper;
import com.coherentsolutions.pot.insurance.service.ClaimService;
import com.coherentsolutions.pot.insurance.service.CompanyService;
import com.coherentsolutions.pot.insurance.service.EmployeeService;
import com.coherentsolutions.pot.insurance.specifications.ClaimFilterCriteria;
import com.coherentsolutions.pot.insurance.specifications.EmployeeFilterCriteria;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@Getter
@Setter
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class SecurityService {

  @Autowired
  private final ClaimService claimService;
  private final ClaimMapper claimMapper;
  @Autowired
  private final CompanyService companyService;
  private final CompanyMapper companyMapper;
  @Autowired
  private final EmployeeService employeeService;
  private final EmployeeMapper employeeMapper;


  // ------------------------------------- CLAIMS ----------------------------------------------
  public boolean canGetAllClaims(Authentication authentication) {
    if (hasRole(authentication, "insuranceAdmin")) {
      return true;
    }
    if(hasRole(authentication, "companyAdmin")){
      ClaimFilterCriteria claimFilterCriteria = new ClaimFilterCriteria();
      claimFilterCriteria.setCompany(getUserCompanyName(authentication));
      return claimFilterCriteria.getCompany().equals(getUserCompanyName(authentication));
    }
    else {
      throw new UnauthorizedAccessException("You are not authorized to access this");
    }
  }

  public boolean canAccessFilteredSortedClaims(Authentication authentication,
      ClaimFilterCriteria claimFilterCriteria) {
    if (hasRole(authentication, "insuranceAdmin")) {
      return true;
    }
    if (hasRole(authentication, "companyAdmin")) {
      claimFilterCriteria.setCompany(getUserCompanyName(authentication));
      return true;
    }
    else{
      throw new UnauthorizedAccessException("You are not authorized to access this");
    }
  }

  public boolean canAccessClaim(Authentication authentication, UUID claimId) {
    assert claimService != null;
    ClaimDTO claim = claimService.getClaimById(claimId);
    ClaimEntity claimEntity = claimMapper.INSTANCE.dtoToEntity(claim);
    if (hasRole(authentication, "insuranceAdmin")) {
      return true;
    }
    if (hasRole(authentication, "companyAdmin")) {
      if (isUserCompanyAdmin(authentication, claimEntity.getCompany())) {
        return true;
      } else {
        throw new UnauthorizedAccessException("You are not authorized to access this");
      }
    }
    if (hasRole(authentication, "user")) {
      if (claimEntity.getEmployee().getUserName().equals(getUserName(authentication))) {
        return true;
      } else {
        throw new UnauthorizedAccessException("You are not authorized to access this");
      }
    } else {
      throw new UnauthorizedAccessException("You are not authorized to access this");
    }
  }

  public boolean canAddClaim(Authentication authentication, ClaimDTO claimDTO) {
    assert claimService != null;
    ClaimEntity claimEntity = claimMapper.INSTANCE.dtoToEntity(claimDTO);
    if (hasRole(authentication, "insuranceAdmin")) {
      return true;
    }
    if (hasRole(authentication, "companyAdmin")) {
      if (isUserCompanyAdmin(authentication, claimEntity.getCompany())) {
        return true;
      } else {
        throw new UnauthorizedAccessException("You are not authorized to access this");
      }
    } else {
      throw new UnauthorizedAccessException("You are not authorized to access this");
    }
  }

  public boolean canUpdateClaim(Authentication authentication, UUID claimId) {
    assert claimService != null;
    ClaimDTO claim = claimService.getClaimById(claimId);
    ClaimEntity claimEntity = claimMapper.INSTANCE.dtoToEntity(claim);
    if (hasRole(authentication, "insuranceAdmin")) {
      return true;
    }
    if (hasRole(authentication, "companyAdmin")) {
      if (isUserCompanyAdmin(authentication, claimEntity.getCompany())) {
        return true;
      } else {
        throw new UnauthorizedAccessException("You are not authorized to access this");
      }
    } else {
      throw new UnauthorizedAccessException("You are not authorized to access this");
    }
  }

  public boolean canDeactivateClaim(Authentication authentication, UUID claimId) {
    assert claimService != null;
    ClaimDTO claim = claimService.getClaimById(claimId);
    ClaimEntity claimEntity = claimMapper.INSTANCE.dtoToEntity(claim);
    if (hasRole(authentication, "insuranceAdmin")) {
      return true;
    }
    if (hasRole(authentication, "companyAdmin")) {
      if (isUserCompanyAdmin(authentication, claimEntity.getCompany())) {
        return true;
      } else {
        throw new UnauthorizedAccessException("You are not authorized to access this");
      }
    } else {
      throw new UnauthorizedAccessException("You are not authorized to access this");
    }
  }

  public boolean canAccessClaimsByEmployee(Authentication authentication, String employeeUserName) {
    if (hasRole(authentication, "insuranceAdmin")) {
      return true;
    }
    if (hasRole(authentication, "companyAdmin")) {
      List<ClaimEntity> claims = mapClaims(employeeUserName);
      if (!claims.isEmpty()) {
        if (isUserCompanyAdmin(authentication, claims.getFirst().getCompany())) {
          return true;
        } else {
          throw new UnauthorizedAccessException("You are not authorized to access this");
        }
      }
    }
    if (hasRole(authentication, "user")) {
      if (employeeUserName.equals(getUserName(authentication))) {
        return true;
      } else {
        throw new UnauthorizedAccessException("You are not authorized to access this");
      }
    } else {
      throw new UnauthorizedAccessException("You are not authorized to access this");
    }
  }

  public boolean canAccessClaimsByCompany(Authentication authentication, String companyName) {
    if (hasRole(authentication, "insuranceAdmin")) {
      return true;
    }
    if (hasRole(authentication, "companyAdmin")) {
      if (companyName.equals(getUserCompanyName(authentication))) {
        return true;
      } else {
        throw new UnauthorizedAccessException("You are not authorized to access this");
      }
    } else {
      throw new UnauthorizedAccessException("You are not authorized to access this");
    }
  }

  // ------------------------------------- UTILITIES ----------------------------------------------
  private boolean hasRole(Authentication authentication, String role) {
    if (authentication instanceof JwtAuthenticationToken jwtAuth) {
      Collection<GrantedAuthority> authorities = jwtAuth.getAuthorities();
      for (GrantedAuthority authority : authorities) {
        if (authority.getAuthority().equals("ROLE_" + role)) {
          return true;
        }
      }
    }
    return false;
  }

  private boolean isUserCompanyAdmin(Authentication authentication, CompanyEntity company) {
    if (authentication instanceof JwtAuthenticationToken jwtAuth) {
      Jwt jwt = jwtAuth.getToken();
      String companyName = jwt.getClaim("given_name");
      return company.getName().equals(companyName);
    }
    throw new UnauthorizedAccessException("You are not authorized to access this");
  }

  private String getUserCompanyName(Authentication authentication) {
    if (authentication instanceof JwtAuthenticationToken jwtAuth) {
      Jwt jwt = jwtAuth.getToken();
      return jwt.getClaim("given_name");
    }
    throw new UnauthorizedAccessException("You are not authorized to access this");
  }

  private String getUserName(Authentication authentication) {
    if (authentication instanceof JwtAuthenticationToken jwtAuth) {
      Jwt jwt = jwtAuth.getToken();
      return jwt.getClaim("preferred_username");
    }
    throw new UnauthorizedAccessException("You are not authorized to access this");
  }

  public List<ClaimEntity> mapClaims(String employeeUserName) {
    assert claimService != null;
    List<ClaimDTO> claims = claimService.getAllClaimsByEmployeeUserName(employeeUserName);
    return claimMapper.INSTANCE.dtosToEntities(claims);
  }


  // ------------------------------------- COMPANIES ----------------------------------------------
  public boolean canGetAllCompanies(Authentication authentication) {
    if (hasRole(authentication, "insuranceAdmin")) {
      return true;
    } else {
      throw new UnauthorizedAccessException("You are not authorized to access this");
    }
  }

  public boolean canCreateCompany(Authentication authentication) {
    if (hasRole(authentication, "insuranceAdmin")) {
      return true;
    } else {
      throw new UnauthorizedAccessException("You are not authorized to access this");
    }
  }

  public boolean canAccessCompany(Authentication authentication, UUID companyId) {
    assert companyService != null;
    CompanyDTO companyDto = companyService.getCompanyById(companyId);
    CompanyEntity company = CompanyMapper.INSTANCE.toEntity(companyDto);
    if (hasRole(authentication, "insuranceAdmin") ||
        (hasRole(authentication, "companyAdmin") && isUserCompanyAdmin(authentication, company))) {
      return true;
    } else {
      throw new UnauthorizedAccessException("You are not authorized to access this");
    }
  }

  public boolean canAccessFilteredSortedCompanies(Authentication authentication) {
    if (hasRole(authentication, "insuranceAdmin")) {
      return true;
    } else {
      throw new UnauthorizedAccessException("You are not authorized to access this");
    }
  }

  public boolean canUpdateCompany(Authentication authentication, UUID companyId) {
    assert companyService != null;
    CompanyEntity company = companyMapper.INSTANCE.toEntity(
        companyService.getCompanyById(companyId));
    if (hasRole(authentication, "insuranceAdmin") ||
        (hasRole(authentication, "companyAdmin") && isUserCompanyAdmin(authentication, company))) {
      return true;
    } else {
      throw new UnauthorizedAccessException("You are not authorized to access this");
    }
  }

  public boolean canDeactivateCompany(Authentication authentication) {
    if (hasRole(authentication, "insuranceAdmin")) {
      return true;
    } else {
      throw new UnauthorizedAccessException("You are not authorized to access this");
    }
  }


  // ------------------------------------- EMPLOYEES ----------------------------------------------
  public boolean canGetAllEmployees(Authentication authentication) {
    if (hasRole(authentication, "insuranceAdmin")) {
      return true;
    }
    if(hasRole(authentication, "companyAdmin")){
      EmployeeFilterCriteria employeeFilterCriteria = new EmployeeFilterCriteria();
      employeeFilterCriteria.setCompanyName(getUserCompanyName(authentication));
      return employeeFilterCriteria.getCompanyName().equals(getUserCompanyName(authentication));
    }
    else {
      throw new UnauthorizedAccessException("You are not authorized to access this");
    }
  }

  public boolean canGetFilteredSortedEmployees(Authentication authentication,
      EmployeeFilterCriteria employeeFilterCriteria) {
    if (hasRole(authentication, "insuranceAdmin")) {
      return true;
    }
    if (hasRole(authentication, "companyAdmin")) {
      employeeFilterCriteria.setCompanyName(getUserCompanyName(authentication));
      return true;
    }
    throw new UnauthorizedAccessException("You are not authorized to access this");
  }

  public boolean canAccessEmployee(Authentication authentication, UUID employeeId) {
    assert employeeService != null;
    EmployeeDTO employee = employeeService.getEmployee(employeeId);
    EmployeeEntity employeeEntity = employeeMapper.INSTANCE.employeeDTOToEmployee(employee);
    if (hasRole(authentication, "insuranceAdmin")) {
      return true;
    }
    if (hasRole(authentication, "companyAdmin")) {
      if (employeeEntity.getCompanyName().equals(getUserCompanyName(authentication))) {
        return true;
      } else {
        throw new UnauthorizedAccessException("You are not authorized to access this");
      }
    }
    if (hasRole(authentication, "user")) {
      if (employeeEntity.getUserName().equals(getUserName(authentication))) {
        return true;
      } else {
        throw new UnauthorizedAccessException("You are not authorized to access this");
      }
    } else {
      throw new UnauthorizedAccessException("You are not authorized to access this");
    }
  }

  public boolean canAddEmployee(Authentication authentication, EmployeeDTO employeeDTO) {
    EmployeeEntity employee = employeeMapper.INSTANCE.employeeDTOToEmployee(employeeDTO);
    if (hasRole(authentication, "insuranceAdmin")) {
      return true;
    }
    if (hasRole(authentication, "companyAdmin")) {
      if (employee.getCompanyName().equals(getUserCompanyName(authentication))) {
        return true;
      } else {
        throw new UnauthorizedAccessException("You are not authorized to access this");
      }
    } else {
      throw new UnauthorizedAccessException("You are not authorized to access this");
    }
  }

  public boolean canUpdateEmployee(Authentication authentication, UUID employeeId) {
    assert employeeService != null;
    EmployeeDTO employee = employeeService.getEmployee(employeeId);
    EmployeeEntity employeeEntity = employeeMapper.INSTANCE.employeeDTOToEmployee(employee);
    if (hasRole(authentication, "insuranceAdmin")) {
      return true;
    }
    if (hasRole(authentication, "companyAdmin")) {
      if (employeeEntity.getCompanyName().equals(getUserCompanyName(authentication))) {
        return true;
      } else {
        throw new UnauthorizedAccessException("You are not authorized to access this");
      }
    }
    if (hasRole(authentication, "user")) {
      if (employeeEntity.getUserName().equals(getUserName(authentication))) {
        return true;
      } else {
        throw new UnauthorizedAccessException("You are not authorized to access this");
      }
    }
    else {
      throw new UnauthorizedAccessException("You are not authorized to access this");
    }
  }

  public boolean canDeactivateEmployee(Authentication authentication, UUID employeeId) {
    assert employeeService != null;
    EmployeeDTO employee = employeeService.getEmployee(employeeId);
    EmployeeEntity employeeEntity = employeeMapper.INSTANCE.employeeDTOToEmployee(employee);
    if (hasRole(authentication, "insuranceAdmin")) {
      return true;
    }
    if (hasRole(authentication, "companyAdmin")) {
      if (employeeEntity.getCompanyName().equals(getUserCompanyName(authentication))) {
        return true;
      } else {
        throw new UnauthorizedAccessException("You are not authorized to access this");
      }
    } else {
      throw new UnauthorizedAccessException("You are not authorized to access this");
    }
  }


  // ------------------------------------- PACKAGES ----------------------------------------------
  public boolean canGetAllPackages(Authentication authentication) {
    return hasRole(authentication, "insuranceAdmin") ||
        hasRole(authentication, "companyAdmin") ||
        hasRole(authentication, "user");
  }

  public boolean canGetFilteredSortedPackages(Authentication authentication) {
    return hasRole(authentication, "insuranceAdmin") ||
        hasRole(authentication, "companyAdmin") ||
        hasRole(authentication, "user");
  }

  public boolean canAccessPackage(Authentication authentication) {
    return hasRole(authentication, "insuranceAdmin") ||
        hasRole(authentication, "companyAdmin") ||
        hasRole(authentication, "user");
  }

  public boolean canAddPackage(Authentication authentication) {
    return hasRole(authentication, "insuranceAdmin");
  }

  public boolean canUpdatePackage(Authentication authentication) {
    return hasRole(authentication, "insuranceAdmin");
  }

  public boolean canDeletePackage(Authentication authentication) {
    return hasRole(authentication, "insuranceAdmin");
  }

  // ------------------------------------- PLANS ----------------------------------------------
  public boolean canGetAllPlans(Authentication authentication) {
    return hasRole(authentication, "insuranceAdmin") ||
        hasRole(authentication, "companyAdmin") ||
        hasRole(authentication, "user");
  }

  public boolean canGetFilteredSortedPlans(Authentication authentication) {
    return hasRole(authentication, "insuranceAdmin") ||
        hasRole(authentication, "companyAdmin") ||
        hasRole(authentication, "user");
  }

  public boolean canAccessPlan(Authentication authentication) {
    return hasRole(authentication, "insuranceAdmin") ||
        hasRole(authentication, "companyAdmin") ||
        hasRole(authentication, "user");
  }

  public boolean canAddPlan(Authentication authentication) {
    return hasRole(authentication, "insuranceAdmin");
  }

  public boolean canUpdatePlan(Authentication authentication) {
    return hasRole(authentication, "insuranceAdmin");
  }

  public boolean canDeletePlan(Authentication authentication) {
    return hasRole(authentication, "insuranceAdmin");
  }

}
