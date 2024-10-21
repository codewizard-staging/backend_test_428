package com.replicacia.service.impl;

import com.replicacia.model.Permission;
import com.replicacia.model.Role;
import com.replicacia.repo.PermissionRepository;
import com.replicacia.repo.RoleRepository;
import com.replicacia.rest.admin.dto.UpdateRoleRequestDto;
import com.replicacia.service.RoleService;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

  private final RoleRepository roleRepository;
  private final PermissionRepository permissionRepository;

  @Override
  @Transactional
  public Role saveRole(final Role role) {
    return this.roleRepository.save(role);
  }

  @Override
  public Optional<Role> getRoleById(final Long id) {
    return this.roleRepository.findById(id);
  }

  @Override
  public Optional<Role> getRoleByPublicId(final String publicId) {
    return this.roleRepository.findByPublicId(publicId);
  }

  @Override
  public List<Role> getAllRoles() {
    return this.roleRepository.findAll();
  }

  @Override
  public Boolean isRoleExists(final String name) {
    final Optional<Role> roleGroup = this.roleRepository.findByName(name);
    return roleGroup.isPresent();
  }

  @Override
  @Transactional
  public void deleteRoleById(final String id) {
    final Optional<Role> optionalRole = this.roleRepository.findByPublicId(id);
    optionalRole.ifPresent(role -> this.roleRepository.deleteById(role.getId()));
  }

  @Override
  @Transactional
  public void addPermissionsToRole(final String publicId, final List<String> permissionIds) {
    final Optional<Role> roleOptional = this.roleRepository.findByPublicId(publicId);
    if (roleOptional.isPresent()) {
      final Role role = roleOptional.get();
      final List<Permission> permissions =
          this.permissionRepository.findByPublicIdIn(permissionIds);
      role.getPermissions().addAll(permissions);

      this.roleRepository.save(role);
    }
  }

  @Override
  @Transactional
  public void removePermissionsToRole(final String publicId, final List<String> permissionIds) {
    final Optional<Role> roleOptional = this.roleRepository.findByPublicId(publicId);
    if (roleOptional.isPresent()) {
      final Role role = roleOptional.get();
      final List<Permission> permissions =
          this.permissionRepository.findByPublicIdIn(permissionIds);
      permissions.forEach(role.getPermissions()::remove);

      this.roleRepository.save(role);
    }
  }

  @Override
  public Optional<Role> findByName(final String name) {
    return this.roleRepository.findByName(name);
  }

  @Override
  public Role saveRole(final UpdateRoleRequestDto dto) {
    final Optional<Role> roleOptional = this.roleRepository.findByPublicId(dto.getId());
    final Role role = roleOptional.orElseGet(Role::new);
    role.setDescription(dto.getDescription());
    role.setName(dto.getName());

    return this.saveRole(role);
  }
}
