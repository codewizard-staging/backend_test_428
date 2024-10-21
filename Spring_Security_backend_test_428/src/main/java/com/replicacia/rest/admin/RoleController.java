package com.replicacia.rest.admin;

import com.replicacia.exception.ResourceExistsException;
import com.replicacia.exception.ResourceNotFoundException;
import com.replicacia.model.Role;
import com.replicacia.rest.admin.dto.RolePermissionRequestDTO;
import com.replicacia.rest.admin.dto.RoleResponseDTO;
import com.replicacia.rest.admin.dto.UpdateRoleRequestDto;
import com.replicacia.service.RoleService;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping(value = "/app/roles")
@RequiredArgsConstructor
public class RoleController {
  private final RoleService roleService;
  private final ModelMapper mapper;

  @PostMapping
  public ResponseEntity<RoleResponseDTO> createRole(@RequestBody final Role role) {
    final Boolean isRoleExist = this.roleService.isRoleExists(role.getName());
    if (isRoleExist) {
      throw new ResourceExistsException("Role Already Exist with Name: " + role.getName());
    }
    final Role rg = this.roleService.saveRole(role);
    final URI location =
        ServletUriComponentsBuilder.fromPath("/app/roles").buildAndExpand(rg.getId()).toUri();
    final RoleResponseDTO dto = this.mapper.map(rg, RoleResponseDTO.class);

    return ResponseEntity.created(location).body(dto);
  }

  @PutMapping
  public ResponseEntity<RoleResponseDTO> updateRole(@RequestBody final UpdateRoleRequestDto dto) {
    final Boolean isRoleExist = this.roleService.isRoleExists(dto.getName());
    if (isRoleExist) {
      throw new ResourceExistsException("Role Already Exist with Name: " + dto.getName());
    }
    final Role rg = this.roleService.saveRole(dto);
    final RoleResponseDTO responseDTO = this.mapper.map(rg, RoleResponseDTO.class);

    return ResponseEntity.accepted().body(responseDTO);
  }

  @GetMapping("/{id}")
  public ResponseEntity<RoleResponseDTO> getRoleById(@PathVariable final String id) {
    final Optional<Role> rg = this.roleService.getRoleByPublicId(id);

    if (!rg.isPresent()) {
      throw new ResourceNotFoundException("No Role found for given id: " + id);
    }

    return ResponseEntity.accepted().body(this.mapper.map(rg, RoleResponseDTO.class));
  }

  @GetMapping
  public ResponseEntity<List<RoleResponseDTO>> getAllRoles() {
    final List<Role> roles = this.roleService.getAllRoles();

    if (Objects.nonNull(roles) && !roles.isEmpty()) {
      final List<RoleResponseDTO> dtos =
          roles.stream()
              .map(role -> this.mapper.map(role, RoleResponseDTO.class))
              .collect(Collectors.toList());

      return ResponseEntity.accepted().body(dtos);
    }
    throw new ResourceNotFoundException("No roles found");
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteRoleId(@PathVariable final String id) {
    this.roleService.deleteRoleById(id);
  }

  @PutMapping("/{id}/permissions")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void addPermissionsToRole(
      @PathVariable final String id, @RequestBody final RolePermissionRequestDTO requestDTO) {
    this.roleService.addPermissionsToRole(id, requestDTO.getPermissionIds());
  }

  @DeleteMapping("/{id}/permissions")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void removePermissionsToRole(
      @PathVariable final String id, @RequestBody final RolePermissionRequestDTO requestDTO) {
    this.roleService.removePermissionsToRole(id, requestDTO.getPermissionIds());
  }
}
