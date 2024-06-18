package com.coherentsolutions.pot.insurance.service;

import com.coherentsolutions.pot.insurance.entity.ExampleEntity;
import com.coherentsolutions.pot.insurance.repository.ExampleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ExampleService {

  private final ExampleRepository exampleRepository;

  public ExampleEntity addExample(ExampleEntity example) {
    return exampleRepository.save(example);
  }

  public List<ExampleEntity> getAllExamples() {
    return exampleRepository.findAll();
  }

  public ExampleEntity updateExample(Integer id, ExampleEntity updatedExample) {
    return exampleRepository.findById(id)
        .map(example -> {
          example.setName(updatedExample.getName());
          return exampleRepository.save(example);
        })
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "Example with ID " + id + " was not found"));
  }

  public void deleteExample(Integer id) {
    exampleRepository.deleteById(id);
  }
}
