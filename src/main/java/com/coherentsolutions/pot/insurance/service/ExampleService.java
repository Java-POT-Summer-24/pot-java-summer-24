package com.coherentsolutions.pot.insurance.service;

import com.coherentsolutions.pot.insurance.entity.ExampleEntity;
import com.coherentsolutions.pot.insurance.repository.ExampleRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExampleService {

  private final ExampleRepository exampleRepository;

  public ExampleService(ExampleRepository exampleRepository) {
    this.exampleRepository = exampleRepository;
  }

  public ExampleEntity addExample(ExampleEntity example) {
    return exampleRepository.save(example);
  }

  public List<ExampleEntity> getAllExamples() {
    return exampleRepository.findAll();
  }

  public Optional<ExampleEntity> updateExample(Integer id, ExampleEntity updatedExample) {
    return exampleRepository.findById(id)
        .map(example -> {
          example.setName(updatedExample.getName());
          return exampleRepository.save(example);
        });
  }

  public void deleteExample(Integer id) {
    exampleRepository.deleteById(id);
  }
}
