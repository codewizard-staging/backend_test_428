package com.replicacia.service;

import com.replicacia.model.Role;
import com.replicacia.rest.admin.dto.UpdateRoleRequestDto;
import java.util.List;
import java.util.Optional;

public interface RoleService {
  Role saveRole(Role role);

  Optional<Role> getRoleById(Long id);

  Optional<Role> getRoleByPublicId(String publicId);

  List<Role> getAllRoles();

  Boolean isRoleExists(String name);

  void deleteRoleById(String id);

  void addPermissionsToRole(String publicId, List<String> permissionIds);

  void removePermissionsToRole(String publicId, List<String> permissionIds);

  Optional<Role> findByName(String name);

  Role saveRole(UpdateRoleRequestDto dto);
}
