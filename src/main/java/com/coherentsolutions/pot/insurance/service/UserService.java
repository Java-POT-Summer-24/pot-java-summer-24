package com.coherentsolutions.pot.insurance.service;

import com.coherentsolutions.pot.insurance.entity.UserEntity;
import com.coherentsolutions.pot.insurance.entity.CompanyEntity;
import com.coherentsolutions.pot.insurance.exception.NotFoundException;
import com.coherentsolutions.pot.insurance.repository.CompanyRepository;
import com.coherentsolutions.pot.insurance.repository.UserRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final CompanyRepository companyRepository;

  @Transactional
  public void saveUserEntity(UserEntity user) {
    userRepository.save(user);
  }

  public UserEntity getUserEntityById(UUID id) {
    return userRepository.findById(id).orElseThrow(() -> new NotFoundException("UserEntity not found"));
  }

  @Transactional
  public CompanyEntity findOrCreateCompanyByName(String companyName) {
    Optional<CompanyEntity> companyOpt = companyRepository.findByName(companyName);
    if (companyOpt.isPresent()) {
      return companyOpt.get();
    } else {
      CompanyEntity newCompany = new CompanyEntity();
      newCompany.setName(companyName);
      return companyRepository.save(newCompany);
    }
  }
}
