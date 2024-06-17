package com.coherentsolutions.pot.insurance.controller;

import com.coherentsolutions.pot.insurance.entity.ExampleEntity;
import com.coherentsolutions.pot.insurance.service.ExampleService;
import java.util.Optional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/examples")
public class ExampleController {

  private final ExampleService exampleService;

  public ExampleController(ExampleService exampleService) {
    this.exampleService = exampleService;
  }

  @PostMapping
  public ExampleEntity createExample(@RequestBody ExampleEntity example) {
    return exampleService.addExample(example);
  }

  @GetMapping
  public List<ExampleEntity> getAllExamples() {
    return exampleService.getAllExamples();
  }

  @PutMapping("/{id}")
  public Optional<ExampleEntity> updateExample(@PathVariable Integer id, @RequestBody ExampleEntity example) {
    return exampleService.updateExample(id, example);
  }

  @DeleteMapping("/{id}")
  public void deleteExample(@PathVariable Integer id) {
    exampleService.deleteExample(id);
  }
}
