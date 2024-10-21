package com.replicacia.rest.security.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegisterResponseDTO {
  private String userId;
  private String userName;
  private String email;
  private String country;
  private Boolean isEmailVerified;
}
