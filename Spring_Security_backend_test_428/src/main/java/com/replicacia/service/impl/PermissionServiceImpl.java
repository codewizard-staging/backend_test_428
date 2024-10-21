package com.replicacia.service.impl;

import com.replicacia.exception.BadRequestException;
import com.replicacia.exception.ResourceExistsException;
import com.replicacia.exception.ResourceNotFoundException;
import com.replicacia.model.Permission;
import com.replicacia.model.Role;
import com.replicacia.repo.PermissionRepository;
import com.replicacia.repo.RoleRepository;
import com.replicacia.rest.admin.dto.PermissionRequestDTO;
import com.replicacia.service.PermissionService;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

  private final PermissionRepository permissionRepository;
  private final RoleRepository roleRepository;

  @Override
  @Transactional
  public Permission save(final Permission permission) {
    return this.permissionRepository.save(permission);
  }

  @Override
  @Transactional
  public Permission createPermission(final PermissionRequestDTO requestDTO) {
    if (this.isPermissionExist(requestDTO)) {
      throw new ResourceExistsException(
          "Permission Already Exist api: "
              + requestDTO.getApi()
              + ", access Type: "
              + requestDTO.getApiAccess());
    }
    final Optional<Role> roleOptional = this.roleRepository.findByPublicId(requestDTO.getRoleId());

    if (!roleOptional.isPresent()) {
      throw new BadRequestException("Role with Id: " + requestDTO.getRoleId() + " not exist");
    }
    final Permission permission =
        Permission.builder()
            .api(requestDTO.getApi())
            .apiAccess(requestDTO.getApiAccess())
            .name(requestDTO.getName())
            .build();
    final Permission savedPermission = this.permissionRepository.save(permission);

    final Role role = roleOptional.get();
    savedPermission.getRoles().add(role);
    role.getPermissions().add(savedPermission);
    this.roleRepository.save(role);

    return this.permissionRepository.save(savedPermission);
  }

  @Override
  public boolean isPermissionExist(final PermissionRequestDTO requestDTO) {
    final Optional<Permission> permissionOptional =
        this.permissionRepository.findByApiAndApiAccess(
            requestDTO.getApi(), requestDTO.getApiAccess());
    return permissionOptional.isPresent();
  }

  @Override
  public Optional<Permission> findByPublicId(final String id) {
    return this.permissionRepository.findByPublicId(id);
  }

  @Override
  @Transactional
  public void delete(final String publicId) {
    final Optional<Permission> permissionOptional =
        this.permissionRepository.findByPublicId(publicId);
    permissionOptional.ifPresent(
        permission -> this.permissionRepository.deleteById(permission.getId()));
  }

  @Override
  public List<Permission> findAll() {
    return this.permissionRepository.findAll();
  }

  @Override
  @Transactional
  public Permission updatePermission(final PermissionRequestDTO requestDTO) {

    if (this.isPermissionExist(requestDTO)) {
      throw new ResourceExistsException(
          "Permission Already Exist api: "
              + requestDTO.getApi()
              + ", access Type: "
              + requestDTO.getApiAccess());
    }

    final Optional<Permission> permissionOptional =
        this.permissionRepository.findByPublicId(requestDTO.getId());
    if (!permissionOptional.isPresent()) {
      throw new ResourceNotFoundException("No Permission exist for Id: " + requestDTO.getId());
    }

    final Permission permission = permissionOptional.get();
    permission.setApi(requestDTO.getApi());
    permission.setApiAccess(requestDTO.getApiAccess());
    permission.setName(requestDTO.getName());
    permission.setRoles(permission.getRoles());

    return this.permissionRepository.save(permission);
  }

  @Override
  public Optional<Permission> findById(final Long id) {
    return this.permissionRepository.findById(id);
  }
}
