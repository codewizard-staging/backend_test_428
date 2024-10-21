package com.replicacia.rest.admin;

import com.replicacia.exception.ResourceNotFoundException;
import com.replicacia.model.Permission;
import com.replicacia.model.Role;
import com.replicacia.rest.admin.dto.PermissionRequestDTO;
import com.replicacia.rest.admin.dto.PermissionResponseDTO;
import com.replicacia.service.PermissionService;
import java.net.URI;
import java.util.List;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping(value = "/app/permissions")
@RequiredArgsConstructor
public class PermissionController {

  private final PermissionService permissionService;
  private final ModelMapper mapper;

  @PostMapping
  public ResponseEntity<PermissionResponseDTO> createPermission(
      @RequestBody final PermissionRequestDTO requestDTO) {
    final Permission perm = this.permissionService.createPermission(requestDTO);

    final URI location =
        ServletUriComponentsBuilder.fromPath("/").buildAndExpand(perm.getId()).toUri();
    final PermissionResponseDTO responseDTO =
        PermissionResponseDTO.builder()
            .api(perm.getApi())
            .apiAccess(perm.getApiAccess())
            .id(perm.getPublicId())
            .name(perm.getName())
            .roles(perm.getRoles().stream().map(Role::getName).collect(Collectors.toSet()))
            .build();

    return ResponseEntity.created(location).body(responseDTO);
  }

  @PutMapping
  public ResponseEntity<PermissionResponseDTO> updatePermission(
      @RequestBody final PermissionRequestDTO requestDTO) {

    final Permission perm = this.permissionService.updatePermission(requestDTO);

    final URI location =
        ServletUriComponentsBuilder.fromPath("/").buildAndExpand(perm.getId()).toUri();
    final PermissionResponseDTO responseDTO =
        PermissionResponseDTO.builder()
            .api(perm.getApi())
            .apiAccess(perm.getApiAccess())
            .id(perm.getPublicId())
            .name(perm.getName())
            .roles(perm.getRoles().stream().map(Role::getName).collect(Collectors.toSet()))
            .build();

    return ResponseEntity.created(location).body(responseDTO);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deletePermission(@PathVariable(value = "id") final String id) {
    this.permissionService.delete(id);
  }

  @GetMapping("/{id}")
  public ResponseEntity<PermissionResponseDTO> getPermissionById(
      @PathVariable(value = "id") final String id) {
    final Optional<Permission> permissionOptional = this.permissionService.findByPublicId(id);

    if (!permissionOptional.isPresent()) {
      throw new ResourceNotFoundException("No permission found for given id: " + id);
    }

    final Permission permission = permissionOptional.get();
    final PermissionResponseDTO dto = this.mapper.map(permission, PermissionResponseDTO.class);
    dto.setRoles(permission.getRoles().stream().map(Role::getName).collect(Collectors.toSet()));

    return ResponseEntity.ok().body(dto);
  }

  @GetMapping
  public ResponseEntity<List<PermissionResponseDTO>> getPermissions(
      @RequestParam final Optional<String> roleId) {
    final List<Permission> permissions = this.permissionService.findAll();

    final List<PermissionResponseDTO> dtos =
        permissions.stream()
            .filter(
                permission ->
                    !roleId.isPresent()
                        || permission.getRoles().stream()
                            .map(Role::getPublicId)
                            .collect(Collectors.toSet())
                            .contains(roleId.get()))
            .map(
                permission -> {
                  final PermissionResponseDTO dto =
                      this.mapper.map(permission, PermissionResponseDTO.class);
                  dto.setRoles(
                      permission.getRoles().stream()
                          .map(Role::getName)
                          .collect(Collectors.toSet()));
                  return dto;
                })
            .collect(Collectors.toList());

    return ResponseEntity.ok().body(dtos);
  }
}
