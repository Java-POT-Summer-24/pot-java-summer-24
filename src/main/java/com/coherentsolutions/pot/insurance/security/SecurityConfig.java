package com.coherentsolutions.pot.insurance.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

  private static final String REALM_ACCESS_CLAIM = "realm_access";
  private static final String ROLES_CLAIM = "roles";

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http,
      ClientRegistrationRepository clientRegistrationRepository) throws Exception {
    http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).hasRole("insuranceAdmin")
            // ------------------------------------- CLAIMS ------------------------------------------------
            .requestMatchers(HttpMethod.GET, "/v1/claims").hasRole("insuranceAdmin")
            .requestMatchers(HttpMethod.GET, "/v1/claims/filtered")
            .hasAnyRole("insuranceAdmin", "companyAdmin")
            .requestMatchers(HttpMethod.GET, "/v1/claims/{id}")
            .hasAnyRole("insuranceAdmin", "companyAdmin", "user")
            .requestMatchers(HttpMethod.POST, "/v1/claims")
            .hasAnyRole("insuranceAdmin", "companyAdmin")
            .requestMatchers(HttpMethod.PUT, "/v1/claims/{id}")
            .hasAnyRole("insuranceAdmin", "companyAdmin")
            .requestMatchers(HttpMethod.GET, "/v1/claims/companies/{companyName}")
            .hasAnyRole("insuranceAdmin", "companyAdmin")
            .requestMatchers(HttpMethod.GET, "/v1/claims/employees/{employeeUserName}")
            .hasAnyRole("insuranceAdmin", "companyAdmin", "user")
            .requestMatchers(HttpMethod.DELETE, "/v1/claims/{id}")
            .hasAnyRole("insuranceAdmin", "companyAdmin")
            // ------------------------------------- COMPANIES ----------------------------------------------
            .requestMatchers(HttpMethod.GET, "/v1/companies").hasRole("insuranceAdmin")
            .requestMatchers(HttpMethod.POST, "/v1/companies").hasRole("insuranceAdmin")
            .requestMatchers(HttpMethod.GET, "/v1/companies/{id}")
            .hasAnyRole("insuranceAdmin", "companyAdmin")
            .requestMatchers(HttpMethod.GET, "/v1/companies/filtered").hasRole("insuranceAdmin")
            .requestMatchers(HttpMethod.PUT, "/v1/companies/{id}")
            .hasAnyRole("insuranceAdmin", "companyAdmin")
            .requestMatchers(HttpMethod.DELETE, "/v1/companies/{id}")
            .hasAnyRole("insuranceAdmin", "companyAdmin")
            // ------------------------------------- EMPLOYEES ----------------------------------------------
            .requestMatchers(HttpMethod.GET, "/v1/employees").hasRole("insuranceAdmin")
            .requestMatchers(HttpMethod.GET, "/v1/employees/filtered")
            .hasAnyRole("insuranceAdmin", "companyAdmin")
            .requestMatchers(HttpMethod.GET, "/v1/employees/{id}")
            .hasAnyRole("insuranceAdmin", "companyAdmin", "user")
            .requestMatchers(HttpMethod.POST, "/v1/employees")
            .hasAnyRole("insuranceAdmin", "companyAdmin")
            .requestMatchers(HttpMethod.PUT, "/v1/employees/{id}")
            .hasAnyRole("insuranceAdmin", "companyAdmin")
            .requestMatchers(HttpMethod.DELETE, "/v1/employees/{id}")
            .hasAnyRole("insuranceAdmin", "companyAdmin")
            // ------------------------------------- PACKAGES ----------------------------------------------
            .requestMatchers(HttpMethod.GET, "/v1/packages")
            .hasAnyRole("insuranceAdmin", "companyAdmin", "user")
            .requestMatchers(HttpMethod.GET, "/v1/packages/filtered")
            .hasAnyRole("insuranceAdmin", "companyAdmin", "user")
            .requestMatchers(HttpMethod.GET, "/v1/packages/{id}")
            .hasAnyRole("insuranceAdmin", "companyAdmin", "user")
            .requestMatchers(HttpMethod.POST, "/v1/packages").hasRole("insuranceAdmin")
            .requestMatchers(HttpMethod.PUT, "/v1/packages/{id}").hasRole("insuranceAdmin")
            .requestMatchers(HttpMethod.DELETE, "/v1/packages/{id}").hasRole("insuranceAdmin")
            // ------------------------------------- PLANS ----------------------------------------------
            .requestMatchers(HttpMethod.GET, "/v1/plans")
            .hasAnyRole("insuranceAdmin", "companyAdmin", "user")
            .requestMatchers(HttpMethod.GET, "/v1/plans/filtered")
            .hasAnyRole("insuranceAdmin", "companyAdmin", "user")
            .requestMatchers(HttpMethod.GET, "/v1/plans/{id}")
            .hasAnyRole("insuranceAdmin", "companyAdmin", "user")
            .requestMatchers(HttpMethod.POST, "/v1/plans").hasRole("insuranceAdmin")
            .requestMatchers(HttpMethod.PUT, "/v1/plans/{id}").hasRole("insuranceAdmin")
            .requestMatchers(HttpMethod.DELETE, "/v1/plans/{id}").hasRole("insuranceAdmin")
            .anyRequest().authenticated())
        .csrf(csrf -> csrf
            .ignoringRequestMatchers(new AntPathRequestMatcher("/h2-console/**")))
        .headers(headers -> headers
            .frameOptions(FrameOptionsConfig::sameOrigin))
        .oauth2Login(oauth2 -> oauth2
            .clientRegistrationRepository(clientRegistrationRepository))
        .logout(logout -> logout
            .logoutSuccessHandler(oidcLogoutSuccessHandler(clientRegistrationRepository)))
        .oauth2ResourceServer(oauth2 -> oauth2
            .jwt(jwt -> jwt
                .jwtAuthenticationConverter(jwtAuthenticationConverter())));
    return http.build();
  }

  @Bean
  public JwtDecoder jwtDecoder() {
    //String issuerUri = System.getenv("KEYCLOAK_AUTH_SERVER_URL:http://localhost:8080/realms/myrealm");
    String issuerUri = "http://localhost:8080/realms/myrealm"; // Working version for now
    return JwtDecoders.fromIssuerLocation(issuerUri);
  }

  @Bean
  public JwtAuthenticationConverter jwtAuthenticationConverter() {
    JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
      Collection<GrantedAuthority> authorities = extractRolesFromClaims(jwt.getClaims());
      return authorities;
    });
    return jwtAuthenticationConverter;
  }

  private Collection<GrantedAuthority> extractRolesFromClaims(Map<String, Object> claims) {
    if (claims.containsKey(REALM_ACCESS_CLAIM)) {
      Map<String, Object> realmAccess = (Map<String, Object>) claims.get(REALM_ACCESS_CLAIM);
      if (realmAccess.containsKey(ROLES_CLAIM)) {
        List<String> roles = (List<String>) realmAccess.get(ROLES_CLAIM);
        return roles.stream()
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
            .collect(Collectors.toList());
      }
    }
    return List.of();
  }

  private LogoutSuccessHandler oidcLogoutSuccessHandler(
      ClientRegistrationRepository clientRegistrationRepository) {
    OidcClientInitiatedLogoutSuccessHandler successHandler =
        new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);

    successHandler.setPostLogoutRedirectUri("{baseUrl}");
    return successHandler;
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.addAllowedOrigin("http://localhost:8080");
    configuration.addAllowedOrigin("http://localhost:8081");
    configuration.addAllowedMethod("GET");
    configuration.addAllowedMethod("POST");
    configuration.addAllowedMethod("PUT");
    configuration.addAllowedMethod("DELETE");
    configuration.addAllowedHeader("Authorization");
    configuration.addAllowedHeader("Content-Type");
    configuration.setAllowCredentials(true);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}