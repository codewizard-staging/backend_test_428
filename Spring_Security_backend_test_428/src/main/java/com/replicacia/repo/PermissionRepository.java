package com.replicacia.repo;

import com.replicacia.model.Permission;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
  Optional<Permission> findByApiAndApiAccess(String api, String apiAccess);

  Optional<Permission> findByPublicId(String publicId);

  List<Permission> findByPublicIdIn(List<String> publicIds);
}
