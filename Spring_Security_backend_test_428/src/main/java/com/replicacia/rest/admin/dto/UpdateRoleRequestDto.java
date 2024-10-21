package com.replicacia.rest.admin.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateRoleRequestDto {
  private String id;
  private String name;
  private String description;
}
