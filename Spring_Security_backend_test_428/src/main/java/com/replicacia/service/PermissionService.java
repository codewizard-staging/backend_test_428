package com.replicacia.service;

import com.replicacia.model.Permission;
import com.replicacia.rest.admin.dto.PermissionRequestDTO;
import java.util.List;
import java.util.Optional;

public interface PermissionService {
  Permission save(Permission role);

  void delete(String publicId);

  List<Permission> findAll();

  Permission updatePermission(PermissionRequestDTO role);

  Optional<Permission> findById(Long id);

  Permission createPermission(PermissionRequestDTO requestDTO);

  boolean isPermissionExist(PermissionRequestDTO requestDTO);

  Optional<Permission> findByPublicId(String id);
}
