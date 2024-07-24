package com.coherentsolutions.pot.insurance.security;

import com.coherentsolutions.pot.insurance.dto.ClaimDTO;
import com.coherentsolutions.pot.insurance.entity.UserEntity;
import com.coherentsolutions.pot.insurance.repository.CompanyRepository;
import com.coherentsolutions.pot.insurance.repository.UserRepository;
import com.coherentsolutions.pot.insurance.service.ClaimService;
import com.coherentsolutions.pot.insurance.service.CompanyService;
import com.coherentsolutions.pot.insurance.specifications.ClaimFilterCriteria;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@Getter
@Setter
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class SecurityService {

  private final ClaimService claimService;
  private final CompanyService companyService;
  private final UserRepository userRepository;
  private final CompanyRepository companyRepository;

  // ------------------------------------- CLAIMS ------------------------------------------------
  public boolean canAccessClaim(Authentication authentication, UUID claimId) {
    assert claimService != null;
    ClaimDTO claim = claimService.getClaimById(claimId);
    return hasRole(authentication, "insuranceAdmin") ||
        (hasRole(authentication, "companyAdmin") && isClaimFromUserCompany(authentication, claim)) ||
        (hasRole(authentication, "user") && isClaimFromUser(authentication, claim));
  }

  public boolean canAddClaim(Authentication authentication, ClaimDTO claimDTO) {
    return hasRole(authentication, "insuranceAdmin") ||
        (hasRole(authentication, "companyAdmin") && isClaimFromUserCompany(authentication, claimDTO));
  }

  public boolean canUpdateClaim(Authentication authentication, UUID claimId) {
    assert claimService != null;
    ClaimDTO claim = claimService.getClaimById(claimId);
    return hasRole(authentication, "insuranceAdmin") ||
        (hasRole(authentication, "companyAdmin") && isClaimFromUserCompany(authentication, claim));
  }

  public boolean canDeactivateClaim(Authentication authentication, UUID claimId) {
    assert claimService != null;
    ClaimDTO claim = claimService.getClaimById(claimId);
    return hasRole(authentication, "insuranceAdmin") ||
        (hasRole(authentication, "companyAdmin") && isClaimFromUserCompany(authentication, claim));
  }

  public boolean canAccessClaimsByEmployee(Authentication authentication, String employeeUserName) {
    return hasRole(authentication, "insuranceAdmin") ||
        (hasRole(authentication, "companyAdmin") && isUserFromCompany(authentication, employeeUserName)) ||
        (hasRole(authentication, "user") && isCurrentUser(authentication, employeeUserName));
  }

  public boolean canAccessClaimsByCompany(Authentication authentication, String companyName) {
    return hasRole(authentication, "insuranceAdmin") ||
        (hasRole(authentication, "companyAdmin") && isCurrentCompany(authentication, companyName));
  }

  public boolean canAccessFilteredSortedClaims(Authentication authentication, ClaimFilterCriteria claimFilterCriteria) {
    if (hasRole(authentication, "insuranceAdmin")) {
      return true;
    }

    if (hasRole(authentication, "companyAdmin")) {
      String userCompany = getUserCompany(authentication);
      return claimFilterCriteria.getCompany().equals(userCompany);
    }

    return false;
  }
  public boolean canGetAllClaims(Authentication authentication) {
    return hasRole(authentication, "insuranceAdmin");
  }
  private boolean hasRole(Authentication authentication, String role) {
    return authentication.getAuthorities().stream()
        .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + role));
  }

  private boolean isClaimFromUser(Authentication authentication, ClaimDTO claim) {
    return claim.getEmployee().equals(authentication.getName());
  }

  private boolean isClaimFromUserCompany(Authentication authentication, ClaimDTO claim) {
    return claim.getCompany().equals(getUserCompany(authentication));
  }

  private boolean isUserFromCompany(Authentication authentication, String employeeUserName) {
    String userCompanyName = getUserCompany(authentication);

    if(userRepository == null)
      return false;

    Optional<UserEntity> userOptional = userRepository.findByEmail(employeeUserName);
    if (userOptional.isEmpty()) {
      return false;
    }

    UserEntity user = userOptional.get();
    return user.getCompany().getName().equals(userCompanyName);
  }

  private boolean isCurrentUser(Authentication authentication, String employeeUserName) {
    return authentication.getName().equals(employeeUserName);
  }

  private boolean isCurrentCompany(Authentication authentication, String companyName) {
    return companyName.equals(getUserCompany(authentication));
  }

  private String getUserCompany(Authentication authentication) {
    String email = authentication.getName();

    if(userRepository == null)
      return null;

    Optional<UserEntity> userOptional = userRepository.findByEmail(email);
    if (userOptional.isEmpty()) {
      return null;
    }

    UserEntity user = userOptional.get();
    return user.getCompany().getName();
  }

  // ------------------------------------- COMPANIES ----------------------------------------------
  public boolean canGetAllCompanies(Authentication authentication) {
    return hasRole(authentication, "insuranceAdmin");
  }

  public boolean canCreateCompany(Authentication authentication) {
    return hasRole(authentication, "insuranceAdmin");
  }

  public boolean canAccessCompany(Authentication authentication, UUID companyId) {
    // company = companyService.getCompanyById(companyId);
    return hasRole(authentication, "insuranceAdmin");
       // || (hasRole(authentication, "companyAdmin") && isUserCompanyAdmin(authentication, company)) // TODO: Company admin implementation
  }

  public boolean canAccessFilteredSortedCompanies(Authentication authentication) {
    return hasRole(authentication, "insuranceAdmin");
  }

  public boolean canUpdateCompany(Authentication authentication, UUID companyId) {
    //CompanyDTO company = companyService.getCompanyById(companyId);
    return hasRole(authentication, "insuranceAdmin");
       // || (hasRole(authentication, "companyAdmin") && isUserCompanyAdmin(authentication, company)); // TODO: Company admin implementation
  }

  public boolean canDeactivateCompany(Authentication authentication) {
    return hasRole(authentication, "insuranceAdmin");
  }

  // ------------------------------------- EMPLOYEES ----------------------------------------------
  public boolean canGetAllEmployees(Authentication authentication) {
    return hasRole(authentication, "insuranceAdmin");
  }

  public boolean canGetFilteredSortedEmployees(Authentication authentication) {
    return hasRole(authentication, "insuranceAdmin") ||
        hasRole(authentication, "companyAdmin");
  }

  public boolean canAccessEmployee(Authentication authentication, UUID employeeId) {
    return hasRole(authentication, "insuranceAdmin") ||
        (hasRole(authentication, "companyAdmin") && isEmployeeFromUserCompany(authentication, employeeId)) ||
        (hasRole(authentication, "user") && isCurrentUser(authentication, employeeId));
  }

  public boolean canAddEmployee(Authentication authentication) {
    return hasRole(authentication, "insuranceAdmin") ||
        hasRole(authentication, "companyAdmin");
  }

  public boolean canUpdateEmployee(Authentication authentication, UUID employeeId) {
    return hasRole(authentication, "insuranceAdmin") ||
        (hasRole(authentication, "companyAdmin") && isEmployeeFromUserCompany(authentication, employeeId));
  }

  public boolean canDeactivateEmployee(Authentication authentication, UUID employeeId) {
    return hasRole(authentication, "insuranceAdmin") ||
        (hasRole(authentication, "companyAdmin") && isEmployeeFromUserCompany(authentication, employeeId));
  }

  private boolean isEmployeeFromUserCompany(Authentication authentication, UUID employeeId) {
    assert userRepository != null;
    Optional<UserEntity> userOptional = userRepository.findById(employeeId);
    if (userOptional.isEmpty()) {
      return false;
    }
    UserEntity user = userOptional.get();
    return user.getCompany().getUsers().contains(user);
  }

  private boolean isCurrentUser(Authentication authentication, UUID employeeId) {
    assert userRepository != null;
    Optional<UserEntity> userOptional = userRepository.findById(employeeId);
    if (userOptional.isEmpty()) {
      return false;
    }
    UserEntity user = userOptional.get();
    return user.getName().equals(authentication.getName());
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

  public boolean canAccessPackage(Authentication authentication, UUID packageId) {
    return hasRole(authentication, "insuranceAdmin") ||
        hasRole(authentication, "companyAdmin") ||
        hasRole(authentication, "user");
  }

  public boolean canAddPackage(Authentication authentication) {
    return hasRole(authentication, "insuranceAdmin");
  }

  public boolean canUpdatePackage(Authentication authentication, UUID packageId) {
    return hasRole(authentication, "insuranceAdmin");
  }

  public boolean canDeletePackage(Authentication authentication, UUID packageId) {
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

  public boolean canAccessPlan(Authentication authentication, UUID planId) {
    return hasRole(authentication, "insuranceAdmin") ||
        hasRole(authentication, "companyAdmin") ||
        hasRole(authentication, "user");
  }

  public boolean canAddPlan(Authentication authentication) {
    return hasRole(authentication, "insuranceAdmin");
  }

  public boolean canUpdatePlan(Authentication authentication, UUID planId) {
    return hasRole(authentication, "insuranceAdmin");
  }

  public boolean canDeletePlan(Authentication authentication, UUID planId) {
    return hasRole(authentication, "insuranceAdmin");
  }

}