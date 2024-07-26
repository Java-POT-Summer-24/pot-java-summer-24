package com.coherentsolutions.pot.insurance.security;

import com.coherentsolutions.pot.insurance.constants.UserRole;
import com.coherentsolutions.pot.insurance.entity.CompanyEntity;
import com.coherentsolutions.pot.insurance.entity.UserEntity;
import com.coherentsolutions.pot.insurance.service.UserService;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;


@Component
@AllArgsConstructor
public class KeycloakLoginListener implements ApplicationListener<AuthenticationSuccessEvent> {

  private final UserService userService;

  @Override
  public void onApplicationEvent(AuthenticationSuccessEvent event) {
    Authentication auth = event.getAuthentication();

    if (auth instanceof KeycloakAuthenticationToken keycloakAuth) {
      String username = keycloakAuth.getName();
      UUID userId = UUID.fromString(keycloakAuth.getAccount().getKeycloakSecurityContext().getToken().getSubject());
      String companyName = getCompanyNameFromUsername(username);

      UserEntity user = new UserEntity();
      user.setId(userId);
      user.setUserName(username);
      user.setRole(UserRole.companyAdmin);

      CompanyEntity company = userService.findOrCreateCompanyByName(companyName);
      user.setCompany(company);

      userService.saveUserEntity(user);
    }
  }

  private String getCompanyNameFromUsername(String username) {
    int index = username.indexOf("Admin");
    if (index != -1) {
      String company = username.substring(0, index);
      if (!company.equalsIgnoreCase("insurance")) {
        return company;
      }
    }
    return username; // Default to username if "Admin" not found
  }
}