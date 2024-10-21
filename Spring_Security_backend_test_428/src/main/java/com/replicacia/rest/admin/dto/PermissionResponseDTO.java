package com.replicacia.rest.admin.dto;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionResponseDTO {
  private String id;
  private String name;
  private String api;
  private String apiAccess;
  private Set<String> roles;
}
