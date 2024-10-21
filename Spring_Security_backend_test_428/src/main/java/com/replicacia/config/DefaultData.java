package com.replicacia.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.replicacia.model.AppUser;
import com.replicacia.model.Permission;
import com.replicacia.model.Role;
import com.replicacia.repo.PermissionRepository;
import com.replicacia.rest.admin.dto.PermissionRequestDTO;
import com.replicacia.rest.security.service.UserService;
import com.replicacia.service.PermissionService;
import com.replicacia.service.RoleService;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DefaultData {

  @Autowired private UserService userService;

  @Autowired private RoleService roleService;

  @Autowired protected ObjectMapper objectMapper;

  @Autowired private PermissionService permissionService;

  @Autowired private PermissionRepository permissionRepository;

  @EventListener
  public void appReady(final ApplicationReadyEvent event) {
    final Map<Long, AppUser> appUserMap = this.createUser();
    final Map<String, Role> roleMap = this.createRole();
    this.createPermissions(roleMap.get("ADMIN"));
    this.assignRole(appUserMap, roleMap.get("ADMIN"));
  }

  private void assignRole(final Map<Long, AppUser> appUserMap, final Role admin) {
    appUserMap.forEach(
        (key, appUser) -> {
          appUser.getRoles().add(admin);
          this.userService.update(appUser);
        });
  }

  private void createPermissions(final Role role) {
    final List<PermissionRequestDTO> requestDTOS;
    try {
      requestDTOS =
          this.objectMapper.readValue(
              new ClassPathResource("permissions.json").getInputStream(),
              new TypeReference<List<PermissionRequestDTO>>() {});
      for (final PermissionRequestDTO dto : requestDTOS) {
        dto.setRoleId(role.getPublicId());
        final Optional<Permission> permissionOptional =
            this.permissionRepository.findByApiAndApiAccess(dto.getApi(), dto.getApiAccess());

        permissionOptional.orElseGet(() -> this.permissionService.createPermission(dto));
      }
    } catch (final IOException e) {
      log.error("Unable to load default data", e);
    }
  }

  private Map<String, Role> createRole() {
    final List<Role> roleList;
    final Map<String, Role> roleMap = new HashMap<>();
    try {
      roleList =
          this.objectMapper.readValue(
              new ClassPathResource("role.json").getInputStream(),
              new TypeReference<List<Role>>() {});
      for (final Role role : roleList) {
        final Optional<Role> roleOptional = this.roleService.findByName(role.getName());
        Role role1 = null;
        role1 = roleOptional.orElseGet(() -> this.roleService.saveRole(role));

        roleMap.put(role1.getName(), role1);
      }
    } catch (final IOException e) {
      log.error("Unable to load default data", e);
    }
    return roleMap;
  }

  private Map<Long, AppUser> createUser() {
    final List<AppUser> userList;
    final Map<Long, AppUser> appUserMap = new HashMap<>();
    try {
      userList =
          this.objectMapper.readValue(
              new ClassPathResource("user.json").getInputStream(),
              new TypeReference<List<AppUser>>() {});
      for (final AppUser user : userList) {
        AppUser appUser = this.userService.findAllByUserName(user.getUsername());
        if (Objects.isNull(appUser)) {
          appUser = this.userService.save(user, false);
          this.userService.verifyUser(appUser, null, false);
        }
        appUserMap.put(appUser.getId(), appUser);
      }
    } catch (final IOException e) {
      log.error("Unable to load default data", e);
    }
    return appUserMap;
  }
}
