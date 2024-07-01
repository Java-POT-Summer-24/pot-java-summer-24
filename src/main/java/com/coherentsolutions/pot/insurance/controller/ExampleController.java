package com.coherentsolutions.pot.insurance.controller;

import com.coherentsolutions.pot.insurance.entity.ExampleEntity;
import com.coherentsolutions.pot.insurance.service.ExampleService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/examples")
@RequiredArgsConstructor
public class ExampleController {

  private final ExampleService exampleService;

  @PostMapping
  public ExampleEntity createExample(@RequestBody ExampleEntity example) {
    return exampleService.addExample(example);
  }

  @GetMapping
  public List<ExampleEntity> getAllExamples() {
    return exampleService.getAllExamples();
  }

  @PutMapping("/{id}")
  public ExampleEntity updateExample(@PathVariable Integer id, @RequestBody ExampleEntity example) {
    return exampleService.updateExample(id, example);
  }

  @DeleteMapping("/{id}")
  public void deleteExample(@PathVariable Integer id) {
    exampleService.deleteExample(id);
  }
}
