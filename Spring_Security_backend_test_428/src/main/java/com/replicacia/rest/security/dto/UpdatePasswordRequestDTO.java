package com.replicacia.rest.security.dto;

import com.replicacia.rest.security.enums.OtpType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePasswordRequestDTO {
  private Integer otp;
  private OtpType type;
  private String newPassword;
  private String verifyPassword;
}
