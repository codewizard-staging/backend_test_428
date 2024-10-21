package com.replicacia.rest.admin.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermissionRequestDTO {
  private String id;
  private String name;
  private String api;
  private String apiAccess;
  private String roleId;
}
