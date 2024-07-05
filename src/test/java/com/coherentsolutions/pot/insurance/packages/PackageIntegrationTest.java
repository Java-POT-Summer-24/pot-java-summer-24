package com.coherentsolutions.pot.insurance.packages;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.coherentsolutions.pot.insurance.constants.PackageStatus;
import com.coherentsolutions.pot.insurance.controller.PackageController;
import com.coherentsolutions.pot.insurance.dto.PackageDTO;
import com.coherentsolutions.pot.insurance.entity.PackageEntity;
import com.coherentsolutions.pot.insurance.mapper.PackageMapper;
import com.coherentsolutions.pot.insurance.repository.PackageRepository;
import com.coherentsolutions.pot.insurance.service.PackageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = PackageController.class)
@TestPropertySource(properties = "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration")
public class PackageIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private PackageService packageService;

  @MockBean
  private PackageRepository packageRepository;

  private final EasyRandom easyRandom = new EasyRandom();

  @Test
  void testGetFilteredSortedPackages() throws Exception {
    List<PackageEntity> packageEntities = easyRandom.objects(PackageEntity.class, 3)
        .toList();
    List<PackageDTO> packageDTOs = packageEntities.stream()
        .map(PackageMapper.INSTANCE::entityToDto)
        .collect(Collectors.toList());

    Page<PackageDTO> pagedPackages = new PageImpl<>(packageDTOs, PageRequest.of(0, 3), packageDTOs.size());

    Mockito.when(packageService.getFilteredSortedPackages(
        any(),
        any()
    )).thenReturn(pagedPackages);

    mockMvc.perform(get("/v1/packages/filtered")
            .param("name", "Package A")
            .param("status", "ACTIVE")
            .param("page", "0")
            .param("size", "3")
            .param("sort", "name,asc")
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(3))
        .andExpect(jsonPath("$.content[0].name").value(packageDTOs.get(0).getName()))
        .andExpect(jsonPath("$.content[1].name").value(packageDTOs.get(1).getName()))
        .andExpect(jsonPath("$.content[2].name").value(packageDTOs.get(2).getName()));

    Mockito.verify(packageService).getFilteredSortedPackages(
        any(),
        any()
    );
  }


  @Test
  void testAddPackage() throws Exception {
    PackageDTO packageDTO = easyRandom.nextObject(PackageDTO.class);
    PackageEntity packageEntity = PackageMapper.INSTANCE.dtoToEntity(packageDTO);

    Mockito.when(packageRepository.save(any())).thenReturn(packageEntity);
    Mockito.when(packageService.addPackage(any())).thenReturn(packageDTO);

    String packageJson = objectMapper.writeValueAsString(packageDTO);
    mockMvc.perform(post("/v1/packages")
            .contentType(MediaType.APPLICATION_JSON)
            .content(packageJson))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value(packageDTO.getName()));
  }

  @Test
  void testGetAllPackages() throws Exception {
    List<PackageEntity> packageEntities = easyRandom.objects(PackageEntity.class, 3).collect(Collectors.toList());
    List<PackageDTO> packageDTOs = packageEntities.stream()
        .map(PackageMapper.INSTANCE::entityToDto)
        .collect(Collectors.toList());

    Mockito.when(packageRepository.findAll()).thenReturn(packageEntities);
    Mockito.when(packageService.getAllPackages()).thenReturn(packageDTOs);

    mockMvc.perform(get("/v1/packages"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(3));
  }

  @Test
  void testGetPackageById() throws Exception {
    UUID packageId = UUID.randomUUID();
    PackageDTO packageDTO = easyRandom.nextObject(PackageDTO.class);
    packageDTO.setId(packageId);
    PackageEntity packageEntity = PackageMapper.INSTANCE.dtoToEntity(packageDTO);

    Mockito.when(packageRepository.findById(packageId)).thenReturn(java.util.Optional.of(packageEntity));
    Mockito.when(packageService.getPackageById(packageId)).thenReturn(packageDTO);

    mockMvc.perform(get("/v1/packages/{id}", packageId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(packageId.toString()));
  }

  @Test
  void testUpdatePackage() throws Exception {
    UUID packageId = UUID.randomUUID();
    PackageDTO originalPackageDTO = easyRandom.nextObject(PackageDTO.class);
    originalPackageDTO.setId(packageId);
    PackageEntity packageEntity = PackageMapper.INSTANCE.dtoToEntity(originalPackageDTO);

    Mockito.when(packageRepository.findById(eq(packageId))).thenReturn(Optional.of(packageEntity));
    Mockito.when(packageRepository.save(any(PackageEntity.class))).thenReturn(packageEntity);
    Mockito.when(packageService.updatePackage(any(PackageDTO.class))).thenReturn(originalPackageDTO);

    String packageJson = objectMapper.writeValueAsString(originalPackageDTO);
    mockMvc.perform(put("/v1/packages")
            .contentType(MediaType.APPLICATION_JSON)
            .content(packageJson))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(packageId.toString()))
        .andExpect(jsonPath("$.name").value(originalPackageDTO.getName()));
  }

  @Test
  void testDeactivatePackage() throws Exception {
    UUID packageId = UUID.randomUUID();
    PackageDTO packageDTO = easyRandom.nextObject(PackageDTO.class);
    packageDTO.setId(packageId);
    packageDTO.setStatus(PackageStatus.DEACTIVATED);
    PackageEntity packageEntity = PackageMapper.INSTANCE.dtoToEntity(packageDTO);

    Mockito.when(packageRepository.findById(packageId))
        .thenReturn(java.util.Optional.of(packageEntity));
    Mockito.when(packageRepository.save(any())).thenReturn(packageEntity);
    Mockito.when(packageService.deactivatePackage(packageId)).thenReturn(packageDTO);

    mockMvc.perform(delete("/v1/packages/{id}", packageId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("DEACTIVATED"));
  }
}