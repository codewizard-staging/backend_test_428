package com.replicacia.repo;

import com.replicacia.model.Role;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

  Optional<Role> findByName(String name);

  Optional<Role> findByPublicId(String publicId);

  List<Role> findByPublicIdIn(List<String> publicIds);
}
